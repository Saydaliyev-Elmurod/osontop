package com.example.test.repository

import com.example.test.domain.CategoryEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface CategoryRepository : R2dbcRepository<CategoryEntity, UUID> {
    fun findByIdAndDeletedFalse(id: UUID): Mono<CategoryEntity>
    fun findAllByDeletedFalse(): Flux<CategoryEntity>
    fun findAllByParentIdAndDeletedFalse(parentId: UUID): Flux<CategoryEntity>
}
