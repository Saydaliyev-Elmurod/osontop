package com.example.test.model.request

import com.example.test.domain.enums.DeviceType

data class AdminLoginRequest(
    val phone: String,
    val password: String,
    val deviceId: String,
    val os: String? = null, // android
    val osVersion: String? = null, // 24
    val brand: String? = null,//Samsung
    val model: String? = null, //SM-S918B
    val deviceType: DeviceType,
    val fcmToken: String? = null
)
