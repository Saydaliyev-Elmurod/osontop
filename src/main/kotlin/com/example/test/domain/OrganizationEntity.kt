package com.example.test.domain

import com.example.test.util.Constant
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(schema = Constant.SCHEMA, name = "organizations")
data class OrganizationEntity(
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var photoUrl: String? = null,
    var ownerId: UUID? = null,
    var isActive: Boolean = true
) : BaseEntity()
