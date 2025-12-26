package com.example.test.model.request

import com.example.test.domain.enums.Gender
import com.example.test.domain.enums.Language
import java.time.LocalDate

data class UpdateUserRequest(
  val imageUrl: String?,
  val firstName: String?,
  val lastName: String?,
  val language: Language?,
  val gender: Gender?,
  val birthday: LocalDate?
)
