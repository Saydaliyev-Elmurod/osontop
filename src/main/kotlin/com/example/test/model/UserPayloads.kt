package com.example.test.model

import com.example.test.domain.Gender
import com.example.test.domain.Language
import java.time.LocalDate

data class UpdateUserRequest(
    val imageUrl: String?,
    val firstName: String?,
    val lastName: String?,
    val language: Language?,
    val gender: Gender?,
    val birthday: LocalDate?
)
