package com.example.test.service

import com.example.test.domain.UserEntity
import com.example.test.domain.UserType
import com.example.test.exception.BadRequestException
import com.example.test.exception.handler.ErrorCode
import com.example.test.model.*
import com.example.test.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey
import kotlin.random.Random

@Service
@Log4j2
class LoginService(
  private val redisTemplate: ReactiveStringRedisTemplate,
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder
) {
  companion object {
    private val LOGGER = LogManager.getLogger()
    private const val CODE_PREFIX = "CODE:"
    private val secretKey: SecretKey =
      Keys.hmacShaKeyFor("your-very-secure-and-long-secret-key-that-is-at-least-256-bits".toByteArray())
  }

  private val buildType = "dev"
  private val webClient = WebClient.create()

  fun sendCode(request: PhoneRequest): Mono<VerificationResponse> {
    val code = if (buildType == "dev") 12345 else Random.nextInt(10000, 99999)
    val id = UUID.randomUUID()

    return redisTemplate.opsForValue()
      .set(CODE_PREFIX + request.phone, code.toString(), Duration.ofMinutes(3))
      .doOnSuccess { LOGGER.info("Generated code for ${request.phone} is $code") }
      .map {
        VerificationResponse(
          phone = request.phone,
          id = id,
          time = 300,
          timestamp = Instant.now()
        )
      }
  }

  fun verifyCode(request: VerificationRequest): Mono<TokenResponse> {
    return redisTemplate.opsForValue().get(CODE_PREFIX + request.phone)
      .flatMap { codeFromRedis ->
        if (codeFromRedis == request.code.toString()) {
          redisTemplate.opsForValue().delete(CODE_PREFIX + request.phone)
            .then(
              userRepository.findByPhoneAndDeletedFalse(request.phone)
                .switchIfEmpty(
                  userRepository.save(
                    UserEntity(
                      phone = request.phone,
                      type = UserType.CLIENT
                    )
                  )
                )
                .flatMap { user ->
                  generateTokens(user, request)
                }
            )
        } else {
          Mono.error(BadRequestException(ErrorCode.CODE_INCORRECT, "Code is not valid"))
        }
      }
      .switchIfEmpty(Mono.error(BadRequestException(ErrorCode.TIME_EXPIRED, "Code not found or expired")))
  }

  fun loginAdmin(request: AdminLoginRequest): Mono<TokenResponse> {
    return userRepository.findByPhoneAndDeletedFalse(request.phone)
      .filter { it.type == UserType.ADMIN }
      .switchIfEmpty(Mono.error(RuntimeException("Admin not found")))
      .flatMap { user ->
        if (passwordEncoder.matches(request.password, user.password)) {
          generateTokens(user, request)
        } else {
          Mono.error(RuntimeException("Invalid password"))
        }
      }
  }

  fun loginWithGoogle(request: GoogleLoginRequest): Mono<TokenResponse> {
    return webClient.get()
      .uri("https://oauth2.googleapis.com/tokeninfo?id_token=${request.token}")
      .retrieve()
      .bodyToMono(Map::class.java)
      .flatMap { response ->
        val email = response["email"] as? String
        val emailVerified = response["email_verified"] as? String

        if (email != null && emailVerified == "true") {
          userRepository.findByEmailAndDeletedFalse(email)
            .switchIfEmpty(
              userRepository.save(
                UserEntity(
                  phone = email, // Temporary mapping
                  email = email,
                  type = UserType.CLIENT,
                  authProvider = "GOOGLE"
                )
              )
            )
            .flatMap { user -> generateTokens(user, request) }
        } else {
          Mono.error(RuntimeException("Invalid Google Token"))
        }
      }
      .onErrorResume {
        Mono.error(RuntimeException("Google authentication failed"))
      }
  }

  private fun generateTokens(user: UserEntity, request: VerificationRequest): Mono<TokenResponse> {
    return Mono.fromCallable {
      val now = Date()
      val validity = if (user.type == UserType.ADMIN) 86400000L else 15778800000L

      deviceS
      val accessToken = Jwts.builder()
        .subject(user.id.toString())
        .claim("phone", user.phone)
        .claim("role", user.type.name)
        .issuedAt(now)
        .expiration(Date(now.time + validity))
        .signWith(secretKey)
        .compact()

      TokenResponse(accessToken)
    }
  }
}
