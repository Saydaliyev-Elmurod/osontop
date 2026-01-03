package com.example.test.model.request

import com.example.test.model.common.TextModel
import java.util.UUID

data class ServicesRequest(
  val id: String,
  val name: TextModel?,
  val parentId: UUID?
)
