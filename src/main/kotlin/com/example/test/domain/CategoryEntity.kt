package com.example.test.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table(name = "categories")
data class CategoryEntity(
    @Id
    var id: UUID? = null,
    var nameUz: String? = "",
    var nameRu: String? = "",
    var nameEn: String? = "",
    var descriptionUz: String? = "",
    var descriptionRu: String? = "",
    var descriptionEn: String? = "",
    var image: String? = "",
    var icon: String? = "",
    var parentId: UUID? = null,
    var orderIndex: Int = 0,
    @CreatedDate
    var createdDate: Instant = Instant.now(),
    @LastModifiedDate
    var lastModifiedDate: Instant = Instant.now(),
    var deleted: Boolean = false,
    @Version
    var version: Long? = 0
)
