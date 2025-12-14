package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface
import com.example.test.exception.handler.ExceptionResponse

open class ForbiddenException : RuntimeException, ExceptionInterface {
    override var code: ErrorCode = ErrorCode.FORBIDDEN_ERROR_CODE
    var exceptionResponse: ExceptionResponse? = null

    constructor() : super()

    constructor(code: ErrorCode) : super() {
        this.code = code
    }

    constructor(message: String) : super(message)

    constructor(exceptionResponse: ExceptionResponse) : super() {
        this.exceptionResponse = exceptionResponse
    }

    constructor(code: ErrorCode, message: String) : super(message) {
        this.code = code
    }

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)

    constructor(
        message: String,
        cause: Throwable,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace)
}
