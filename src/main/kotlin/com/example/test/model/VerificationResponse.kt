package com.example.test.model

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

data class VerificationResponse(
  val phone: String,
  val id: UUID,
  val time: Int,
  val timestamp: Instant
)
