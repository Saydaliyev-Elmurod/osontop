package com.example.test.service

import com.example.test.model.PhoneRequest
import com.example.test.model.VerificationResponse
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import kotlin.math.log

@Service
@Log4j2
class LoginService {
  companion object {
    private val LOGGER = LogManager.getLogger()
  }

  private val buildType = "dev";

  fun sendCode(request: PhoneRequest?): VerificationResponse {

  }
}
