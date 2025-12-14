package com.example.test.context

import com.example.test.repository.SessionRepository
import com.example.test.service.JwtService
import com.example.test.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
  private val jwtService: JwtService,
  private val userService: UserService,
  private val sessionRepository: SessionRepository
) {

  private val publicApiEndpoints = arrayOf(
    "/api/users/v1/users/test",
    "/api/users/v1/local/**",
    "/api/users/v1/users/login",
    "/api/users/v1/users/check",
    "/api/users/v1/users/sign-up",
    "/api/users/v1/users/sign-up/confirm",
    "/api/users/v1/users/recovery",
    "/api/users/v1/users/recovery/confirm",
    "/api/users/v1/users/recovery/password",
    "/api/users/v1/users/google/auth",
    "/api/users/v1/driver/login/check",
    "/api/users/v1/users/admin/login",
    "/api/users/v1/users/forgot/**",
    // Swagger UI endpoints
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/api/users/v3/api-docs/**",
    "/webjars/swagger-ui/**",
    "/v3/api-docs/**"
  )

  @Bean
  fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    return http
      .exceptionHandling { exceptionHandling ->
        exceptionHandling
          .authenticationEntryPoint { exchange, _ ->
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            Mono.empty()
          }
          .accessDeniedHandler { exchange, _ ->
            exchange.response.statusCode = HttpStatus.FORBIDDEN
            Mono.empty()
          }
      }
      .authorizeExchange { exchanges ->
        exchanges
          .pathMatchers(*publicApiEndpoints).permitAll().anyExchange().authenticated()
      }
      .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
      .httpBasic { it.disable() }
      .formLogin { it.disable() }
      .csrf { it.disable() }
      .cors { it.disable() }
      .build()
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  private fun bearerAuthenticationFilter(): AuthenticationWebFilter {
    val authManager = CustomReactiveAuthenticationManager()
    val bearerAuthenticationFilter = AuthenticationWebFilter(authManager)
    bearerAuthenticationFilter.setServerAuthenticationConverter(
      JwtConverter(
        jwtService,
        userService,
        sessionRepository
      )
    )
    return bearerAuthenticationFilter
  }
}
