package com.example.test.repository

import com.example.test.domain.SessionEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID


@Repository
interface SessionRepository : R2dbcRepository<SessionEntity, UUID> {
    fun findByUserIdAndDeviceId(userId: UUID, deviceId: UUID): Mono<SessionEntity>
    fun findByIdAndUserIdAndDeviceId(id: UUID, userId: UUID, deviceId: UUID): Mono<SessionEntity>

    fun deleteByIdAndUserId(sessionId: UUID, userId: UUID): Mono<Void>

    fun deleteByDeviceId(id: UUID): Mono<Void>

    fun deleteByUserId(userId: UUID): Mono<Void>
}
