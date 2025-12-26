package com.example.test.model.request

import com.example.test.exception.handler.ErrorCode
import com.example.test.util.Validator


data class PhoneRequest(
  val phone: String?


) {
  init {
    Validator.isTrue(
      ErrorCode.INVALID_PHONE_NUMBER_CODE,
      phone != null && Validator.isValidPhoneNumber(phone),
      "Invalid phone number"
    )
  }
}
