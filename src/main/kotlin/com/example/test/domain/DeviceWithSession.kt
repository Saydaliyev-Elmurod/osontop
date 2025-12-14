package com.example.test.domain

import java.time.Instant
import java.util.UUID

data class DeviceWithSession(
    val id: UUID,
    val uuid: String,
    val osVersion: String,
    val os: String,
    val model: String,
    val brand: String,
    val type: String,
    val device: String,
    val fcmToken: String?,
    val createdDate: Instant,
    val lastModifiedDate: Instant,
    val deleted: Boolean,

    // SessionEntity fields
    val sessionId: UUID,
    val userId: UUID,
    val deviceId: UUID,
    val timestamp: Instant
)
