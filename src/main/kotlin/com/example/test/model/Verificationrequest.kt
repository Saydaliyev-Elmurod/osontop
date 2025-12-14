package com.example.test.model

import java.util.UUID

data class VerificationRequest(
  val id: UUID,
  val phone: String,
  val code: Int,
  val uuid: String,
  val osVersion: String,
  val os: String,
  val model: String,
  val brand: String,
  val type: String,
  val device: String,
  val fcmToken: String
)
