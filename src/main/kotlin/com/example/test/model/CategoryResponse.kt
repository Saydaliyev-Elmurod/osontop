package com.example.test.model

import com.example.test.model.common.TextModel
import java.time.Instant
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: TextModel,
    val description: TextModel,
    val image: String?,
    val icon: String?,
    val parentId: UUID?,
    val orderIndex: Int,
    val createdDate: Instant,
    val lastModifiedDate: Instant
)
