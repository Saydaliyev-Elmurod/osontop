package com.example.test.api

import com.example.test.context.UserPrincipal
import com.example.test.model.*
import com.example.test.service.LoginService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users/v1")
class UserController(private val loginService: LoginService) {
  @PostMapping("/me")
  fun getMe(
    @AuthenticationPrincipal user: UserPrincipal
  ): Mono<UserResponse> {
    return Mono.just(user.user)
  }
}
