package com.example.test.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

@Service
class ImageService(
    private val s3Client: S3Client
) {

    @Value($$"${application.s3.bucket}")
    private lateinit var bucketName: String





    /**
     * Asosiy funksiya: Faylni qabul qiladi, validatsiya qiladi,
     * 3 xil o'lchamda qayta ishlaydi va parallel ravishda S3 ga yuklaydi.
     */
    suspend fun uploadImage(filePart: FilePart): List<String> = coroutineScope {
        // 1. Validatsiya
        validateImage(filePart)

        // Fayl kontentini xotiraga o'qib olamiz (kichik fayllar uchun)
        // Katta fayllar uchun DataBufferUtils.join(filePart.content()) ishlatish tavsiya etiladi,
        // lekin Thumbnailator baribir InputStream talab qiladi.
        val originalBytes = filePart.content()
            .reduce { d1, d2 -> d1.write(d2); d1 } // Barcha bufferlarni birlashtirish
            .map { buffer ->
                val bytes = ByteArray(buffer.readableByteCount())
                buffer.read(bytes)
                bytes
            }.awaitSingle() // Reactor Mono -> Coroutine

        val uuid = UUID.randomUUID().toString()

        // 2. Qayta ishlash va 3. Parallel yuklash
        // Har bir o'lcham uchun alohida async task ishga tushiramiz
        val largeUpload = async(Dispatchers.IO) {
            processAndUpload(
                originalBytes = originalBytes,
                width = 1080,
                quality = 0.80,
                fileName = "$uuid-large.webp"
            )
        }

        val mediumUpload = async(Dispatchers.IO) {
            processAndUpload(
                originalBytes = originalBytes,
                width = 720,
                quality = 0.75,
                fileName = "$uuid-medium.webp"
            )
        }

        val smallUpload = async(Dispatchers.IO) {
            processAndUpload(
                originalBytes = originalBytes,
                width = 320,
                quality = 0.60,
                fileName = "$uuid-small.webp"
            )
        }
        awaitAll(largeUpload, mediumUpload, smallUpload)
    }

    /**
     * Rasmni o'zgartirish va S3 ga yuklash logikasi.
     * InputStream va OutputStream lar `use` bloki yordamida avtomatik yopiladi.
     */

    private fun processAndUpload(
      originalBytes: ByteArray,
      width: Int,
      quality: Double,
      fileName: String
    ): String {
      // Xotirani tejash uchun "use" ishlatamiz
      ByteArrayInputStream(originalBytes).use { inputStream ->
        ByteArrayOutputStream().use { outputStream ->

          // Thumbnailator endi WebP yozishni biladi (Sejda yordamida)
          Thumbnails.of(inputStream)
            .width(width)
            .outputFormat("webp")    // Endi bu ishlaydi!
            .outputQuality(quality)  // 0.8 kabi sifat
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

    private fun validateImage(filePart: FilePart) {
        val filename = filePart.filename().lowercase()
        val contentType = filePart.headers().contentType?.toString()?.lowercase()

        // Oddiy tekshiruv: kengaytma va content-type
        val allowedExtensions = listOf(".jpg", ".jpeg", ".png", ".webp")
        val allowedTypes = listOf("image/jpeg", "image/png", "image/webp")

        val hasValidExtension = allowedExtensions.any { filename.endsWith(it) }
        val hasValidType = allowedTypes.any { contentType == it }

        if (!hasValidExtension && !hasValidType) {
            throw IllegalArgumentException("Faqat rasm fayllari (JPG, PNG, WebP) qabul qilinadi.")
        }
    }
}
