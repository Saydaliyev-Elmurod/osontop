package com.example.test.repository

import com.example.test.domain.OrganizationEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface OrganizationRepository : R2dbcRepository<OrganizationEntity, UUID> {
    fun findByIdAndDeletedFalse(id: UUID): Mono<OrganizationEntity>
    fun findAllByDeletedFalse(pageable: Pageable): Flux<OrganizationEntity>
    fun findAllByNameContainingIgnoreCaseAndDeletedFalse(name: String, pageable: Pageable): Flux<OrganizationEntity>
}
