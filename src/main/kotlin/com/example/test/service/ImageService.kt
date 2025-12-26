package com.example.test.service

import com.example.test.exception.BadRequestException
import com.example.test.exception.handler.ErrorCode
import com.example.test.model.enums.FileFormat
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

@Service
class ImageService(
  private val s3Client: S3Client
) {
  @Value($$"${application.s3.bucket.images}")
  private lateinit var bucketName: String
  suspend fun uploadImage(filePart: FilePart, formats: List<FileFormat>): String = coroutineScope {
    val originalBytes = validateAndGetBytes(filePart)

    val uuid = UUID.randomUUID().toString()
    val uploadJobs = mutableListOf<Deferred<Any>>()
    if (formats.contains(FileFormat.LARGE)) {
      uploadJobs += async(Dispatchers.IO) {
        processAndUpload(
          originalBytes = originalBytes,
          width = 1080,
          quality = 0.80,
          fileName = "$uuid-large.webp"
        )
      }
    }
    if (formats.contains(FileFormat.MEDIUM)) {
      uploadJobs += async(Dispatchers.IO) {
        processAndUpload(
          originalBytes = originalBytes,
          width = 720,
          quality = 0.75,
          fileName = "$uuid-medium.webp"
        )
      }
    }
    if (formats.contains(FileFormat.SMALL)) {
      uploadJobs += async(Dispatchers.IO) {
        processAndUpload(
          originalBytes = originalBytes,
          width = 320,
          quality = 0.60,
          fileName = "$uuid-small.webp"
        )
      }
    }
    uploadJobs.awaitAll()
    uuid
  }

  private suspend fun validateAndGetBytes(filePart: FilePart): ByteArray {
    val filename = filePart.filename().lowercase()
    val allowedExtensions = listOf(".jpg", ".jpeg", ".png", ".webp")

    if (allowedExtensions.none { filename.endsWith(it) }) {
      throw IllegalArgumentException("Faqat rasm fayllari (JPG, PNG, WebP) qabul qilinadi.")
    }

    val maxSize = 2 * 1024 * 1024
    var currentSize = 0

    val bytes = filePart.content()
      .map { buffer ->
        val readable = buffer.readableByteCount()
        currentSize += readable

        if (currentSize > maxSize) {
          DataBufferUtils.release(buffer)
          throw BadRequestException(ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED, "File hajmi 2MB dan oshmasligi kerak")
        }

        val bytes = ByteArray(readable)
        buffer.read(bytes)
        DataBufferUtils.release(buffer)
        bytes
      }
      .collectList()
      .awaitSingle()
      .fold(ByteArray(0)) { acc, curr -> acc + curr }
    return bytes
  }

  private fun processAndUpload(
    originalBytes: ByteArray,
    width: Int,
    quality: Double,
    fileName: String
  ): String {
    ByteArrayInputStream(originalBytes).use { inputStream ->
      ByteArrayOutputStream().use { outputStream ->
        Thumbnails.of(inputStream)
          .width(width)
          .outputFormat("webp")
          .outputQuality(quality)
          .toOutputStream(outputStream)

        val processedBytes = outputStream.toByteArray()

        val putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .contentType("image/webp")
          .build()

        s3Client.putObject(
          putObjectRequest,
          RequestBody.fromBytes(processedBytes)
        )

        return fileName
      }
    }
  }
}
