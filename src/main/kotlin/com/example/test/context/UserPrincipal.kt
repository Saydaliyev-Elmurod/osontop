package com.example.test.context

import com.example.test.model.response.UserResponse
import java.util.UUID


data class UserPrincipal(
  val user: UserResponse,
  val deviceId: UUID,
  val sessionId: UUID
)

