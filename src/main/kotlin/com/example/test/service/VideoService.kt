package com.example.test.service

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

    private val logger = LoggerFactory.getLogger(VideoService::class.java)


    suspend fun uploadVideo(filePart: FilePart): String = coroutineScope {
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

            // 3. Transcoding (FFmpeg)
            logger.info("Starting transcoding for $uuid")
            transcodeToHls(inputFile, workDir)
            logger.info("Transcoding finished for $uuid")

            // 4. Fayllarni S3 ga parallel yuklash
            logger.info("Starting upload for $uuid")
            uploadHlsFiles(workDir, uuid)
            logger.info("Upload finished for $uuid")

            return@coroutineScope uuid

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
            throw RuntimeException("FFmpeg transcoding failed with exit code $exitCode")
        }
    }

    private suspend fun uploadHlsFiles(workDir: Path, uuid: String) = coroutineScope {
        // Papkadagi barcha .m3u8 va .ts fayllarni topish
        val filesToUpload = withContext(Dispatchers.IO) {
            Files.list(workDir).use { stream ->
                stream.filter { path ->
                    val name = path.fileName.toString()
                    name.endsWith(".m3u8") || name.endsWith(".ts")
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
        val contentType = if (file.toString().endsWith(".m3u8")) "application/x-mpegURL" else "video/MP2T"

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
