package com.example.test.util

import com.example.test.exception.InvalidArgumentException
import com.example.test.exception.handler.ErrorCode

object Validator {
  fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return phoneNumber.matches(Regex("^\\+998\\d{9}$"))
  }

  fun notNull(errorCode: ErrorCode, req: Any?, message: String) {
    if (req == null) {
      throw InvalidArgumentException(code = errorCode, message)
    }
  }

  fun isTrue(
    errorCode: ErrorCode, expression: Boolean, message: String
  ) {
    if (!expression) {
      throw InvalidArgumentException(errorCode, message)
    }
  }
}