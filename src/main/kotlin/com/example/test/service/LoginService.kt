package com.example.test.service

import com.example.test.domain.UserEntity
import com.example.test.domain.UserType
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
    val code = if (buildType == "dev") 1234 else Random.nextInt(1000, 9999)
    val id = UUID.randomUUID()

    return redisTemplate.opsForValue()
      .set(CODE_PREFIX + request.phone, code.toString(), Duration.ofMinutes(5))
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
                  generateTokens(user)
                }
            )
        } else {
          Mono.error(RuntimeException("Code is not valid"))
        }
      }
      .switchIfEmpty(Mono.error(RuntimeException("Code not found or expired")))
  }

  fun loginAdmin(request: AdminLoginRequest): Mono<TokenResponse> {
    return userRepository.findByPhoneAndDeletedFalse(request.phone)
      .filter { it.type == UserType.ADMIN }
      .switchIfEmpty(Mono.error(RuntimeException("Admin not found")))
      .flatMap { user ->
        if (passwordEncoder.matches(request.password, user.password)) {
          generateTokens(user)
        } else {
          Mono.error(RuntimeException("Invalid password"))
        }
      }
  }

  fun loginWithGoogle(request: GoogleLoginRequest): Mono<TokenResponse> {
    // Verify token with Google
    return webClient.get()
      .uri("https://oauth2.googleapis.com/tokeninfo?id_token=${request.token}")
      .retrieve()
      .bodyToMono(Map::class.java)
      .flatMap { response ->
        val email = response["email"] as? String
        val emailVerified = response["email_verified"] as? String // Google returns string "true"

        if (email != null && emailVerified == "true") {
          userRepository.findByEmailAndDeletedFalse(email)
            .switchIfEmpty(
              // Create new user if not exists. Note: Phone is mandatory in UserEntity,
              // but for Google login we might not have it.
              // Assuming we can use email as phone or handle it differently.
              // For now, I'll use email as phone placeholder or we need to change UserEntity to make phone nullable.
              // But the requirement said "phone boyicha unique".
              // I will assume for now we might need to ask for phone later or use a dummy.
              // Let's use email as phone for now to satisfy the constraint or create a user with empty phone if allowed?
              // UserEntity has `var phone: String`.
              userRepository.save(
                UserEntity(
                  phone = email, // Temporary mapping
                  email = email,
                  type = UserType.CLIENT,
                  authProvider = "GOOGLE"
                )
              )
            )
            .flatMap { user -> generateTokens(user) }
        } else {
          Mono.error(RuntimeException("Invalid Google Token"))
        }
      }
      .onErrorResume {
        Mono.error(RuntimeException("Google authentication failed"))
      }
  }

  private fun generateTokens(user: UserEntity): Mono<TokenResponse> {
    return Mono.fromCallable {
      val now = Date()
      val validity = if (user.type == UserType.ADMIN) 86400000L else 15778800000L // 1 day vs 6 months (approx)

      val accessToken = Jwts.builder()
        .subject(user.id.toString())
        .claim("phone", user.phone)
        .claim("role", user.type.name)
        .issuedAt(now)
        .expiration(Date(now.time + validity))
        .signWith(secretKey)
        .compact()

      val refreshToken = Jwts.builder()
        .subject(user.id.toString())
        .issuedAt(now)
        .expiration(Date(now.time + validity * 2)) // Refresh token longer? Or same as requirement?
        // Requirement: "adminga 1 kun userga 6 oyga token generatsiya qilib beradi."
        // Usually refresh token is longer, but let's stick to the validity for access token or make refresh token same/longer.
        // I'll make refresh token same duration for simplicity based on prompt, or slightly longer.
        .signWith(secretKey)
        .compact()

      TokenResponse(accessToken, refreshToken)
    }
  }
}
