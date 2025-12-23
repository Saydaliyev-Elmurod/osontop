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
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.UUID

@Service
class ImageService(
    private val s3Client: S3Client
) {

    @Value("\${application.s3.bucket}")
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

        // Barcha yuklashlar tugashini kutamiz
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
        // ByteArrayInputStream - xotiradan o'qish uchun
        ByteArrayInputStream(originalBytes).use { inputStream ->
            // ByteArrayOutputStream - natijani yozish uchun
            ByteArrayOutputStream().use { outputStream ->

                // Thumbnailator yordamida resize va format o'zgartirish
                Thumbnails.of(inputStream)
                    .width(width)
                    .outputFormat("webp")
                    .outputQuality(quality)
                    .toOutputStream(outputStream)

                val processedBytes = outputStream.toByteArray()

                // S3 ga yuklash
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
