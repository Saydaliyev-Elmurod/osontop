package com.example.test.api

import com.example.test.context.UserPrincipal
import com.example.test.model.request.UpdateUserRequest
import com.example.test.model.response.UserResponse
import com.example.test.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users/v1")
class UserController(
  private val userService: UserService
) {

  @PostMapping("/me")
  fun getMe(
    @AuthenticationPrincipal user: UserPrincipal
  ): Mono<UserResponse> {
    return Mono.just(user.user)
  }

  @PatchMapping
  suspend fun updateUser(
    @AuthenticationPrincipal user: UserPrincipal,
    @RequestBody request: UpdateUserRequest
  ): UserResponse {
    return userService.updateUser(user.user.id, request)
  }

  @DeleteMapping
  fun deleteUser(
    @AuthenticationPrincipal user: UserPrincipal
  ): Mono<Void> {
    return userService.deleteUser(user.user.id)
  }
}
