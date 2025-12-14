package com.example.test.api

import com.example.test.model.PhoneRequest
import com.example.test.model.TokenResponse
import com.example.test.model.VerificationRequest
import com.example.test.model.VerificationResponse
import com.example.test.service.LoginService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/login/v1")
class LoginController(private val loginService: LoginService) {

  @PostMapping("/code")
  fun sendCode(
    @RequestBody request: PhoneRequest
  ): Mono<VerificationResponse> {
    return loginService.sendCode(request)
  }

  @PostMapping("/verify")
  fun verifyCode(
    @RequestBody request: VerificationRequest
  ): Mono<TokenResponse> {
    return loginService.verifyCode(request)
  }
}
