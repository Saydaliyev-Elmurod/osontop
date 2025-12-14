package com.example.test.repository

import com.example.test.domain.UserEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@Repository
interface UserRepository : R2dbcRepository<UserEntity, UUID> {

    fun findByIdAndDeletedIsFalse(id: UUID): Mono<UserEntity>

    fun findByAuthIdAndDeletedFalse(authId: String): Mono<UserEntity>

    fun findByPhoneAndDeletedFalse(phone: String): Mono<UserEntity>

    fun findByEmailAndDeletedFalse(email: String): Mono<UserEntity>

    fun existsByPhoneAndDeletedFalse(phone: String): Mono<Boolean>

    fun existsByEmailAndDeletedFalse(email: String): Mono<Boolean>

    fun findAllByIdInAndDeletedIsFalse(idList: List<UUID>): Flux<UserEntity>
}
