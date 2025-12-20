package com.example.test.model

import com.example.test.model.common.TextModel
import java.util.UUID

data class CategoryRequest(
    val name: TextModel,
    val description: TextModel,
    val image: String,
    val icon: String,
    val parentId: UUID? = null,
    val orderIndex: Int = 0
)
