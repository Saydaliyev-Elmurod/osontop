package com.example.test.domain

import com.example.test.util.Constant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Table(schema = Constant.SCHEMA, name = Constant.USER_TABLE)
data class UserEntity(
    @Id
    var id: UUID? = null,
    var firstName: String? = "",
    var lastName: String? = "",
    var phone: String? = null,
    var email: String? = "",
    var password: String? = "",
    var authProvider: String? = "",
    var type: UserType = UserType.CLIENT,
    var imageUrl: String? = null,
    var language: Language? = null,
    var gender: Gender? = null,
    var birthday: LocalDate? = null,
    @CreatedDate
    var createdDate: Instant = Instant.now(),
    @LastModifiedDate
    var lastModifiedDate: Instant = Instant.now(),
    var deleted: Boolean = false
)
