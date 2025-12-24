package com.example.test.model

import java.time.Instant
import java.util.*

data class SmsCache(
  val phone: String?,
  val id: UUID,
  val time: Int,
  val timestamp: Instant,
  val code: Int
)
