package com.example.test.repository

import com.example.test.domain.DeviceEntity
import com.example.test.domain.DeviceWithSession
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@Repository
interface DeviceRepository : R2dbcRepository<DeviceEntity, UUID> {
  fun findByDeviceIdAndDeletedIsFalse(uuid: String): Mono<DeviceEntity>
}
