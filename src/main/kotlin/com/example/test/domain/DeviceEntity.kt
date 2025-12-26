package com.example.test.domain

import com.example.test.domain.enums.DeviceType
import com.example.test.util.Constant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table(schema = Constant.SCHEMA, name = Constant.DEVICES_TABLE)
data class DeviceEntity(
  var deviceId: String? = null,
  var osVersion: String? = null,
  var os: String? = null,
  var model: String? = null,
  var brand: String? = null,
  var deviceType: DeviceType? = DeviceType.WEB,
  var fcmToken: String?,
) : BaseEntity()
