package com.example.test.api

import com.example.test.model.VideoUploadResult
import com.example.test.service.ImageService
import com.example.test.service.VideoService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/files/v1")
class FileController(
  private val videoService: VideoService,
  private val imageService: ImageService
) {

  @PostMapping("/video")
  suspend fun video(@RequestPart("file") filePart: FilePart): VideoUploadResult {
    return videoService.uploadVideo(filePart)
  }

  @PostMapping("/image")
  suspend fun image(@RequestPart("file") filePart: FilePart): List<String> {
    return imageService.uploadImage(filePart)
  }


}
