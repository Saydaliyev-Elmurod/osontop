package com.example.test.api

import com.example.test.service.ImageService
import com.example.test.service.VideoService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/files/v1")
class FileController(
  private val videoService: VideoService,
  private val imageService: ImageService
) {

  @PostMapping("/image")
  suspend fun video(@RequestPart("file") filePart: FilePart): String {
    return videoService.uploadVideo(filePart)
  }

  @PostMapping("/video")
  suspend fun image(@RequestPart("file") filePart: FilePart): List<String> {
    return imageService.uploadImage(filePart)
  }
}
