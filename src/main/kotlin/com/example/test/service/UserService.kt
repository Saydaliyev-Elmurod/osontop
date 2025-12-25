package com.example.test.service

import com.example.test.domain.Role
import com.example.test.model.UserResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class UserService {
  fun a() {}

  fun findById(userId: UUID): Mono<UserResponse> {
    return Mono.just(UserResponse(name = "", Role.ADMIN))
  }

}
