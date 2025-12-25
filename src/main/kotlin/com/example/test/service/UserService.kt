package com.example.test.service

import com.example.test.domain.Role
import com.example.test.mapper.UserMapper
import com.example.test.model.UserResponse
import com.example.test.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository, private val userMapper: UserMapper) {
  fun findById(userId: UUID): Mono<UserResponse> {
    return userRepository.findByIdAndDeletedFalse(userId).map {
      userMapper.toResponse(it)
    }
  }

}
