package com.example.test.model.common

import java.util.UUID

interface CategoryInterface {
  val name: TextModel?
  val description: TextModel?
  val image: String?
  val icon: String?
  val parentId: UUID?
  val orderIndex: Int
}

