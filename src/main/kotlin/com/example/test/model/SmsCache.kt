package com.example.test.model

import org.bouncycastle.util.Integers
import java.time.Instant
import java.util.UUID

data class SmsCache(
  val phone: String?,
  val id: UUID,
  val time: Int,
  val timestamp: Instant,
  val code: Int
)
