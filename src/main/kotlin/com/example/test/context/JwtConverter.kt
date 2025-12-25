package com.example.test.context

import com.example.test.exception.NotFoundException
import com.example.test.repository.SessionRepository
import com.example.test.service.JwtService
import com.example.test.service.UserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

class JwtConverter(
  private val jwtService: JwtService,
  private val userService: UserService,
  private val sessionRepository: SessionRepository
) : ServerAuthenticationConverter {

  override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
    return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
      .filter { it.startsWith("Bearer ") }
      .map { it.substring(7) }
      .flatMap(this::createAuthentication)
      .onErrorResume { Mono.empty() }
  }

  private fun createAuthentication(token: String): Mono<Authentication> {
    return Mono.fromCallable { jwtService.getAllClaims(token) }
      .flatMap { claims ->
        val userId = claims.getUUID("userId")
        val deviceId = claims.getUUID("deviceId")
        val sessionId = claims.getUUID("sessionId")

        sessionRepository.findByIdAndUserIdAndDeviceId(sessionId, userId, deviceId)
          .switchIfEmpty(Mono.error(JwtException("Session not found")))
          .flatMap { session ->
            userService.findById(session.userId)
              .switchIfEmpty(Mono.error(NotFoundException("User not found")))
              .map { userResponse ->
                val principal = UserPrincipal(
                  user = userResponse,
                  deviceId = deviceId,
                  sessionId = sessionId
                )
                CustomAuthenticationToken(principal, listOf(SimpleGrantedAuthority(userResponse.role.name)))
              }
          }
      }
  }

  private fun Claims.getUUID(key: String): UUID {
    return try {
      UUID.fromString(this.get(key, String::class.java))
    } catch (_: Exception) {
      throw IllegalArgumentException("Invalid UUID in JWT claim: $key")
    }
  }
}
