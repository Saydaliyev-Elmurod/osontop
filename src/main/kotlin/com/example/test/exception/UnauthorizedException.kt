package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface
import com.example.test.exception.handler.ExceptionResponse

open class UnauthorizedException : RuntimeException, ExceptionInterface {
    override var code: ErrorCode = ErrorCode.UNAUTHORIZED_ERROR_CODE
    var exceptionResponse: ExceptionResponse? = null

    constructor() : super()

    constructor(exceptionResponse: ExceptionResponse) : super() {
        this.exceptionResponse = exceptionResponse
    }

    constructor(message: String) : super(message)

    constructor(code: ErrorCode) : super() {
        this.code = code
    }

    constructor(code: ErrorCode, message: String) : super(message) {
        this.code = code
    }

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}
