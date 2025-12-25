package com.example.test.service

import com.example.test.model.VideoUploadResult
import com.example.test.util.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

@Service
class VideoService(
  private val s3Client: S3Client
) {

  @Value("\${application.s3.bucket}")
  private lateinit var bucketName: String

  @Value("\${application.video.temp-dir:/tmp/video-processing}")
  private lateinit var tempDirBase: String

  private val logger = logger<VideoService>()!!

  suspend fun uploadVideo(filePart: FilePart): VideoUploadResult = coroutineScope {
    val uuid = UUID.randomUUID().toString()
    val workDir = Path.of(tempDirBase, uuid)

    try {
      // 1. Vaqtincha papka yaratish
      withContext(Dispatchers.IO) {
        Files.createDirectories(workDir)
      }

      // 2. Input faylni saqlash
      val inputFile = workDir.resolve("input.mp4")
      // FilePart.transferTo() eng optimal usul, chunki u to'g'ridan-to'g'ri diskka yozadi (Zero-copy)
      filePart.transferTo(inputFile).awaitSingle()

      // 3. Thumbnail va HLS jarayonlari (Parallel)
      logger.info("Starting processing for $uuid")

      val thumbnailJob = async { generateThumbnail(inputFile, workDir) }
      val hlsJob = async { transcodeToHls(inputFile, workDir) }

      thumbnailJob.await()
      hlsJob.await()

      logger.info("Processing finished for $uuid")

      // 4. Fayllarni S3 ga parallel yuklash
      logger.info("Starting upload for $uuid")
      uploadFiles(workDir, uuid)
      logger.info("Upload finished for $uuid")

      return@coroutineScope VideoUploadResult(
        hlsUrl = "videos/$uuid/master.m3u8",
        posterUrl = "videos/$uuid/poster.webp"
      )

    } catch (e: Exception) {
      logger.error("Error processing video $uuid", e)
      throw e
    } finally {
      // 5. Cleanup - har qanday holatda ham vaqtincha fayllarni o'chirish
      withContext(Dispatchers.IO) {
        cleanup(workDir)
      }
    }
  }

  private suspend fun generateThumbnail(inputFile: Path, workDir: Path) = withContext(Dispatchers.IO) {
    val posterFile = workDir.resolve("poster.webp").absolutePathString()

    // FFmpeg command for thumbnail
    // -ss 00:00:01 : Seek to 1 second
    // -vframes 1 : Output 1 frame
    // -vf scale=720:-1 : Resize width to 720px, keep aspect ratio
    // -q:v 50 : Quality (for webp, 0-100, higher is better, but ffmpeg mapping might vary, usually for jpeg/webp qscale is used)
    // For webp in ffmpeg, -q:v maps to quality.

    val command = listOf(
      "ffmpeg",
      "-i", inputFile.absolutePathString(),
      "-ss", "00:00:01",
      "-vframes", "1",
      "-vf", "scale=720:-1",
      "-c:v", "libwebp",
      "-q:v", "50",
      posterFile
    )

    runProcess(command, workDir)
  }

  private suspend fun transcodeToHls(inputFile: Path, workDir: Path) = withContext(Dispatchers.IO) {
    val masterPlaylist = workDir.resolve("master.m3u8").absolutePathString()
    val segmentFilename = workDir.resolve("segment_%03d.ts").absolutePathString()

    // FFmpeg komandasi
    val command = listOf(
      "ffmpeg",
      "-i", inputFile.absolutePathString(),
      "-codec:v", "libx264",
      "-codec:a", "aac",
      "-hls_time", "10",
      "-hls_playlist_type", "vod",
      "-hls_segment_filename", segmentFilename,
      masterPlaylist
    )

    runProcess(command, workDir)
  }

  private fun runProcess(command: List<String>, workDir: Path) {
    val processBuilder = ProcessBuilder(command)
    processBuilder.directory(workDir.toFile())
    processBuilder.redirectErrorStream(true) // stderr ni stdout ga yo'naltirish

    val process = processBuilder.start()

    // Process outputini o'qish (log uchun va buffer to'lib qolmasligi uchun)
    val reader = process.inputStream.bufferedReader()
    reader.forEachLine { line ->
      // Loglarni juda ko'paytirmaslik uchun faqat error yoki muhimlarini yozish mumkin
      // logger.debug(line)
    }

    val exitCode = process.waitFor()
    if (exitCode != 0) {
      throw RuntimeException("FFmpeg process failed with exit code $exitCode. Command: $command")
    }
  }

  private suspend fun uploadFiles(workDir: Path, uuid: String) = coroutineScope {
    // Papkadagi barcha kerakli fayllarni topish (.m3u8, .ts, .webp)
    val filesToUpload = withContext(Dispatchers.IO) {
      Files.list(workDir).use { stream ->
        stream.filter { path ->
          val name = path.fileName.toString()
          name.endsWith(".m3u8") || name.endsWith(".ts") || name.endsWith(".webp")
        }.toList()
      }
    }

    // Parallel yuklash
    val uploadJobs = filesToUpload.map { file ->
      async(Dispatchers.IO) {
        val key = "videos/$uuid/${file.fileName}"
        uploadFileToS3(file, key)
      }
    }

    uploadJobs.awaitAll()
  }

  private fun uploadFileToS3(file: Path, key: String) {
    val contentType = when {
      file.toString().endsWith(".m3u8") -> "application/x-mpegURL"
      file.toString().endsWith(".webp") -> "image/webp"
      else -> "video/MP2T"
    }

    val putObjectRequest = PutObjectRequest.builder()
      .bucket(bucketName)
      .key(key)
      .contentType(contentType)
      .build()

    // RequestBody.fromFile() faylni stream qilib o'qiydi, RAM ga to'liq yuklamaydi
    s3Client.putObject(putObjectRequest, RequestBody.fromFile(file))
  }

  private fun cleanup(workDir: Path) {
    try {
      if (workDir.exists()) {
        Files.walk(workDir)
          .sorted(Comparator.reverseOrder()) // Ichki fayllarni birinchi o'chirish
          .forEach { Files.delete(it) }
      }
    } catch (e: Exception) {
      logger.error("Failed to cleanup temp directory: $workDir", e)
    }
  }
}
