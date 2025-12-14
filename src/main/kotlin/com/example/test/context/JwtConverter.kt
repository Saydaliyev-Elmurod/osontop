package com.example.test.context

import com.example.test.repository.SessionRepository
import com.example.test.service.JwtService
import com.example.test.service.UserService
import io.jsonwebtoken.Claims
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

// TODO: UserPrincipal, CurrentUserAuthenticationToken, JwtAuthenticationException importlarini loyihadagi haqiqiy paketlarga o'zgartiring.


class JwtConverter(
  private val jwtService: JwtService,
  private val userService: UserService,
  private val sessionRepository: SessionRepository
) : ServerAuthenticationConverter {

  override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
    return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
      .filter { it.startsWith("Bearer ") }
      .map { it.substring(7) } // "Bearer " qismini olib tashlash
      .flatMap(this::createAuthentication)
      .onErrorResume { error ->
        Mono.empty()
      }
  }

  private fun createAuthentication(token: String): Mono<Authentication> {
    return Mono.fromCallable { jwtService.getAllClaims(token) }
      .onErrorMap { e -> JwtAuthenticationException("Invalid token: ${e.message}", HttpStatus.UNAUTHORIZED) }
      .flatMap { claims ->
        val userId = claims.getUUID("userId")
        val deviceId = claims.getUUID("deviceId")
        val sessionId = claims.getUUID("sessionId")

        sessionRepository.findByIdAndUserIdAndDeviceId(sessionId, userId, deviceId)
          .switchIfEmpty(Mono.error(JwtAuthenticationException("Invalid session credentials", HttpStatus.UNAUTHORIZED)))
      }
      .flatMap { session ->
        Mono.just(CurrentUserAuthenticationToken(session.userId.toString()))
      }
  }


  private fun Claims.getUUID(key: String): UUID {
    return try {
      UUID.fromString(this.get(key, String::class.java))
    } catch (e: Exception) {
      throw IllegalArgumentException("Invalid UUID in JWT claim: $key")
    }
  }
}

class JwtAuthenticationException(message: String, val status: HttpStatus) : RuntimeException(message)

class CurrentUserAuthenticationToken(private val principal: Any) : Authentication {
  override fun getName(): String = principal.toString()
  override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
  override fun getCredentials(): Any? = null
  override fun getDetails(): Any? = null
  override fun getPrincipal(): Any = principal
  override fun isAuthenticated(): Boolean = true
  override fun setAuthenticated(isAuthenticated: Boolean) {
  }
}
