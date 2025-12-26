package com.example.test.model.response

import java.time.Instant
import java.util.UUID

data class VerificationResponse(
  val phone: String?,
  val id: UUID,
  val time: Int,
  val timestamp: Instant
)
