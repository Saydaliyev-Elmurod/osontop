package com.example.test.model.response

import com.example.test.model.common.TextModel
import java.time.Instant
import java.util.UUID

data class ServicesResponse(
  val id: UUID,
  val createdDate: Instant,
  val lastModifiedDate: Instant,
  val name: TextModel?,
  val parentId: UUID?
)
