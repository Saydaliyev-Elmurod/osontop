package com.example.test.model.request

import com.example.test.model.common.CategoryInterface
import com.example.test.model.common.TextModel
import java.util.UUID


data class CategoryRequest(
  val id: String,
  override val name: TextModel?,
  override val description: TextModel?,
  override val image: String?,
  override val icon: String?,
  override val parentId: UUID?,
  override val orderIndex: Int
) : CategoryInterface
