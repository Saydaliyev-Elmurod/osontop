package com.example.test.model

import com.example.test.domain.Gender
import com.example.test.domain.Language
import com.example.test.domain.UserType
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class UserResponse(
    val id: UUID,
    var firstName: String? = "",
    var lastName: String? = "",
    var phone: String? = null,
    var email: String? = "",
    var type: UserType,
    var imageUrl: String? = null,
    var language: Language? = null,
    var gender: Gender? = null,
    var birthday: LocalDate? = null,
    var createdDate: Instant?,
    var lastModifiedDate: Instant?
)
