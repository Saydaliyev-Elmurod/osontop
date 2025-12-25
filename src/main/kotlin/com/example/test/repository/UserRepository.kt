package com.example.test.repository

import com.example.test.domain.UserEntity
import com.example.test.domain.UserType
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@Repository
interface UserRepository : R2dbcRepository<UserEntity, UUID> {
  fun findByPhoneAndDeletedFalse(phone: String): Mono<UserEntity>
  fun findByPhoneAndTypeAndDeletedFalse(phone: String, type: UserType): Mono<UserEntity>
  fun findByEmailAndDeletedFalse(email: String): Mono<UserEntity>
  fun findByIdAndDeletedFalse(id: UUID): Mono<UserEntity>
}
