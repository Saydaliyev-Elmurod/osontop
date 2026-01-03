package com.example.test.repository

import com.example.test.domain.ServicesEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface ServicesRepository : R2dbcRepository<ServicesEntity, UUID> {
    fun findByIdAndDeletedFalse(id: UUID): Mono<ServicesEntity>
    fun findAllByDeletedFalse(): Flux<ServicesEntity>
    fun findAllByParentIdAndDeletedFalse(parentId: UUID): Flux<ServicesEntity>
}
