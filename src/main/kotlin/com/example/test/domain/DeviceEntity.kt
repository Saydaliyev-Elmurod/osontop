package com.example.test.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table(name = "devices")
data class DeviceEntity(
  @Id
  var id: UUID? = null,
  var deviceId: String,
  var osVersion: String,
  var os: String,
  var model: String,
  var brand: String,
  var deviceType: DeviceType,
  var fcmToken: String?,
  @CreatedDate
  var createdDate: Instant = Instant.now(),
  @LastModifiedDate
  var lastModifiedDate: Instant = Instant.now(),
  var deleted: Boolean = false
)
