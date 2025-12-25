package com.example.test.model

import com.example.test.model.common.TextModel
import java.util.UUID

data class CategoryRequest(
  val name: TextModel,
  val description: TextModel,
  val image: String? = null,
  val icon: String? = null,
  val parentId: UUID? = null,
  val orderIndex: Int = 0
)
