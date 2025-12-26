package com.example.test.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.UUID

abstract class BaseEntity {
  @Id
  var id: UUID? = null

  @CreatedDate
  var createdDate: Instant = Instant.now()

  @LastModifiedDate
  var lastModifiedDate: Instant = Instant.now()
  var deleted: Boolean = false
}
