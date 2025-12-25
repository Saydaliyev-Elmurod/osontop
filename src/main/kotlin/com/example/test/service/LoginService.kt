package com.example.test.service

import com.example.test.domain.DeviceEntity
import com.example.test.domain.SessionEntity
import com.example.test.domain.UserEntity
import com.example.test.domain.UserType
import com.example.test.exception.BadRequestException
import com.example.test.exception.handler.ErrorCode
import com.example.test.mapper.DeviceMapper
import com.example.test.model.*
import com.example.test.repository.DeviceRepository
import com.example.test.repository.SessionRepository
import com.example.test.repository.UserRepository
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.random.Random

@Service
@Log4j2
class LoginService(
  private val redisTemplate: ReactiveRedisTemplate<String, SmsCache>,
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val deviceRepository: DeviceRepository,
  private val sessionRepository: SessionRepository,
  private val deviceMapper: DeviceMapper,
  private val jwtService: JwtService,
) {
  companion object {
    private val LOGGER = LogManager.getLogger()
    private const val CODE_PREFIX = "CODE:"
  }

  private val buildType = "dev"
  private val webClient = WebClient.create()

  fun sendCode(request: PhoneRequest): Mono<VerificationResponse> {
    val key = CODE_PREFIX + request.phone

    return redisTemplate.opsForValue()
      .get(key)
      .cast(SmsCache::class.java)
      .map { sms ->
        VerificationResponse(
          phone = request.phone,
          id = sms.id,
          time = 180,
          timestamp = sms.timestamp
        )
      }
      .switchIfEmpty(
        Mono.defer {
          val code = if (buildType == "dev") 12345 else Random.nextInt(10000, 99999)
          val id = UUID.randomUUID()

          val response = VerificationResponse(
            phone = request.phone,
            id = id,
            time = 180,
            timestamp = Instant.now()
          )
          val cache = SmsCache(
            phone = request.phone,
            id = id,
            time = response.time,     // 180 sekund
            timestamp = response.timestamp, // Hozirgi vaqt
            code = code
          )
          redisTemplate.opsForValue()
            .set(key, cache, Duration.ofMinutes(3))
            .doOnSuccess { LOGGER.info("Generate code for ${request.phone} ") }
            .thenReturn(response)
        }
      )
  }

  @Transactional
  fun verifyCode(request: VerificationRequest): Mono<TokenResponse> {
    return redisTemplate.opsForValue().get(CODE_PREFIX + request.phone)
      .flatMap { codeFromRedis ->
        if (codeFromRedis.code == request.code) {
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
                  toggleDevice(request)
                    .flatMap { device ->
                      sessionRepository.deleteByDeviceId(device.id!!)
                        .then(Mono.just(device))
                    }
                    .flatMap { device ->
                      val session = SessionEntity(
                        userId = user.id!!,
                        deviceId = device.id!!
                      )
                      sessionRepository.save(session)
                    }
                    .flatMap { session ->
                      jwtService.generateTokens(user, session)
                    }
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
          jwtService.generateTokens(user, null)
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
            .flatMap { user ->
              ///todo
              jwtService.generateTokens(user, null)
            }
        } else {
          Mono.error(RuntimeException("Invalid Google Token"))
        }
      }
      .onErrorResume {
        Mono.error(RuntimeException("Google authentication failed"))
      }
  }

  private fun toggleDevice(request: VerificationRequest): Mono<DeviceEntity> {
    LOGGER.debug("Toggle Device By UUID: {}", request.deviceId)
    return deviceRepository
      .findByDeviceIdAndDeletedIsFalse(request.deviceId)
      .switchIfEmpty(Mono.just(deviceMapper.toDeviceEntity(request)))
      .map { device -> deviceMapper.toDeviceEntity(device.id, request) }
      .flatMap { device -> deviceRepository.save(device) }
  }


}
