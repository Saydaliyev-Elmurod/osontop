package com.example.test.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table(name = "users")
data class UserEntity(
  @Id
  var id: UUID? = null,
  var firstName: String? = "",
  var lastName: String? = "",
  var phone: String,
  var email: String? = "",
  var password: String? = "",
  var authProvider: String? = "",
  @CreatedDate
  var createdDate: Instant = Instant.now(),
  @LastModifiedDate
  var lastModifiedDate: Instant = Instant.now(),
  var deleted: Boolean = false,
  @Version
  var version: Long? = 0
)
