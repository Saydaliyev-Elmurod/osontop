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
  fun findByIdAndDeletedIsFalse(id: UUID): Mono<DeviceEntity>

  @Query(
    """
        select d.*
        from user_schema.devices d 
        where d.uuid = :uuid
          and deleted = false
         order by created_date desc limit 1
    """
  ) // TODO: "user_schema" va "devices" ni haqiqiy konstantalar bilan almashtiring
  fun findByUuidAndDeletedIsFalse(uuid: String): Mono<DeviceEntity>

  @Query(
    """
        select d.*, s.id as sessionId, user_id, s.device_id, timestamp
        from devices d,
             sessions s
        where s.device_id = d.id
          and user_id = :userId
    """
  )
  fun findAllSessions(userId: UUID): Flux<DeviceWithSession>
}
