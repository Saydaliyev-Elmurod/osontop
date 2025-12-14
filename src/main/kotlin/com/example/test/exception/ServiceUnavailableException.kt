package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class ServiceUnavailableException : RuntimeException, ExceptionInterface {
    override val code = ErrorCode.SERVICE_UNAVAILABLE_ERROR_CODE

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
