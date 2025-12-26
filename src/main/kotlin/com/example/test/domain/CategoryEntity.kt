package com.example.test.domain

import com.example.test.util.Constant
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(schema = Constant.SCHEMA, name = Constant.CATEGORY_TABLE)
class CategoryEntity(
  var nameUz: String? = "",
  var nameRu: String? = "",
  var nameEn: String? = "",
  var descriptionUz: String? = "",
  var descriptionRu: String? = "",
  var descriptionEn: String? = "",
  var image: String? = "",
  var icon: String? = "",
  var parentId: UUID? = null,
  var orderIndex: Int = 0
) : BaseEntity()
