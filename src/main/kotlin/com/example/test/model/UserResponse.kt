package com.example.test.model

import com.example.test.domain.UserType
import java.time.Instant
import java.util.UUID


data class UserResponse(
  val id: UUID,
  var firstName: String? = "",
  var lastName: String? = "",
  var phone: String? = null,
  var email: String? = "",
  var type: UserType,
  var createdDate: Instant?,
  var lastModifiedDate: Instant?
)
