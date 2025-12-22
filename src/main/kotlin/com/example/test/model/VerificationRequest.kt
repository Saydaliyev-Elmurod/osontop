package com.example.test.model

import com.example.test.domain.DeviceType
import java.util.UUID

data class VerificationRequest(
  val id: UUID,
  val phone: String,
  val code: Int,
  val deviceId: String,
  val os: String? = null, // android
  val osVersion: String? = null, // 24
  val brand: String? = null,//Samsung
  val model: String? = null, //SM-S918B
  val deviceType: DeviceType,
  val fcmToken: String? = null
)
