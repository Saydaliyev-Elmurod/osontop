package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class ApiException(message: String, override val code: ErrorCode) : RuntimeException(message), ExceptionInterface
