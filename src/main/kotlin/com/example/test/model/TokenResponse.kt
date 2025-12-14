package com.example.test.model

import java.util.UUID

data class TokenResponse(
  val token: String,
  val role: String,
  val merchantId: UUID
)
