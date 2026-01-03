package com.example.test.domain

import com.example.test.util.Constant
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(schema = Constant.SCHEMA, name = Constant.SERVICES_TABLE)
class ServicesEntity(
  var nameUz: String? = "",
  var nameRu: String? = "",
  var nameEn: String? = "",
  var parentId: UUID? = null
) : BaseEntity()
