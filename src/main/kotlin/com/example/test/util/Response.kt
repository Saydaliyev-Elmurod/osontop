package com.example.test.util

import com.example.test.exception.handler.ErrorCode

data class Response(
  val errorCode: ErrorCode?=null,
  val errorMessage: String? = null,
  val errorDescription: String? = null,
  val success: Boolean? = null,
  val response: Any? = null
)
