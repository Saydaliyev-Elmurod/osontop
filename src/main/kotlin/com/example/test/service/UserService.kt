package com.example.test.service

import com.example.test.mapper.UserMapper
import com.example.test.model.request.UpdateUserRequest
import com.example.test.model.response.UserResponse
import com.example.test.repository.UserRepository
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
  private val userRepository: UserRepository,
  private val userMapper: UserMapper
) {

  fun findById(userId: UUID): Mono<UserResponse> {
    return userRepository.findByIdAndDeletedFalse(userId).map {
      userMapper.toResponse(it)
    }
  }

  @Transactional
  suspend fun updateUser(userId: UUID, request: UpdateUserRequest): UserResponse {
    val user = userRepository.findByIdAndDeletedFalse(userId).awaitSingleOrNull()
    val updatedUser = userMapper.toUpdate(user!!, request)
    val savedUser = userRepository.save(updatedUser).awaitSingle()
    return userMapper.toResponse(savedUser)
  }

  @Transactional
  fun deleteUser(userId: UUID): Mono<Void> {
    return userRepository.findByIdAndDeletedFalse(userId)
      .flatMap { user ->
        user.deleted = true
        userRepository.save(user)
      }
      .then()
  }
}
