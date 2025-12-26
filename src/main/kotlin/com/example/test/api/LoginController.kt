package com.example.test.api

import com.example.test.model.request.AdminLoginRequest
import com.example.test.model.request.GoogleLoginRequest
import com.example.test.model.request.PhoneRequest
import com.example.test.model.request.VerificationRequest
import com.example.test.model.response.TokenResponse
import com.example.test.model.response.VerificationResponse
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

    @PostMapping("/admin")
    fun loginAdmin(
        @RequestBody request: AdminLoginRequest
    ): Mono<TokenResponse> {
        return loginService.loginAdmin(request)
    }

    @PostMapping("/google")
    fun loginWithGoogle(
        @RequestBody request: GoogleLoginRequest
    ): Mono<TokenResponse> {
        return loginService.loginWithGoogle(request)
    }
}
