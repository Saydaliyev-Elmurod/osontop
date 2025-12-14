package com.example.test.exception.handler

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExceptionResponse(
  val code: ErrorCode?,
  val status: String,
  val path: String,
  val errorMessage: String?,
  val errorDescription: String?,
  val timestamp: String
)
