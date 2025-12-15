package com.example.test.service

import com.example.test.domain.UserEntity
import com.example.test.model.PhoneRequest
import com.example.test.model.TokenResponse
import com.example.test.model.VerificationRequest
import com.example.test.model.VerificationResponse
import com.example.test.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
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
  private val jwtService: JwtService,
  private val userRepository: UserRepository
) {
  companion object {
    private val LOGGER = LogManager.getLogger()
    private const val CODE_PREFIX = "CODE:"
    private val secretKey: SecretKey =
      Keys.hmacShaKeyFor("your-very-secure-and-long-secret-key-that-is-at-least-256-bits".toByteArray())
  }

  private val buildType = "dev"

  fun sendCode(request: PhoneRequest): Mono<VerificationResponse> {
    return redisTemplate.opsForValue().get(CODE_PREFIX + request.phone)
      .flatMap {
        Mono.just(
          VerificationResponse(
            phone = request.phone,
            id = UUID.randomUUID(),
            time = 300,
            timestamp = Instant.now()
          )
        )
      }
      .switchIfEmpty(
        Mono.defer {
          val code = if (buildType == "dev") 1234 else Random.nextInt(1000, 9999)
          val id = UUID.randomUUID()
          redisTemplate.opsForValue()
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
      )
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
                      role = "USER"
                    )
                  )
                )
                .flatMap { user ->
                  val deviceId = UUID.randomUUID()
                  val sessionId = UUID.randomUUID()
                  val accessToken = jwtService.generateToken(
                    userId = user.id!!,
                    deviceId = deviceId,
                    sessionId = sessionId,
                    iss = "osontop"
                  )
                  Mono.just(TokenResponse(accessToken))
                }
            )
        } else {
          Mono.error(RuntimeException("Code is not valid"))
        }
      }
      .switchIfEmpty(Mono.error(RuntimeException("Code not found or expired")))
  }
}
