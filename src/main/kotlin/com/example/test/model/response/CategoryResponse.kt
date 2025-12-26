package com.example.test.model.response

import com.example.test.model.common.CategoryInterface
import com.example.test.model.common.TextModel
import java.time.Instant
import java.util.UUID

data class CategoryResponse(
  val id: UUID,
  val createdDate: Instant,
  val lastModifiedDate: Instant,
  override val name: TextModel?,
  override val description: TextModel?,
  override val image: String?,
  override val icon: String?,
  override val parentId: UUID?,
  override val orderIndex: Int
) : CategoryInterface
