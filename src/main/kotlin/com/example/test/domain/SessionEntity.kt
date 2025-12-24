package com.example.test.domain

import com.example.test.util.Constant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table(schema = Constant.SCHEMA,name = Constant.SESSION_TABLE)
data class SessionEntity(
  @Id
  var id: UUID? = null,
  var userId: UUID,
  var deviceId: UUID,
  @CreatedDate
  var timestamp: Instant = Instant.now()
)
