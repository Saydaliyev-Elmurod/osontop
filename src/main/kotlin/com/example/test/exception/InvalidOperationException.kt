package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class InvalidOperationException : RuntimeException, ExceptionInterface {
    override var code: ErrorCode = ErrorCode.INVALID_OPERATION_ERROR_CODE

    constructor(code: ErrorCode) : super() {
        this.code = code
    }

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(code: ErrorCode, message: String, cause: Throwable) : super(message, cause) {
        this.code = code
    }

    constructor(message: String) : super(message)

    constructor(code: ErrorCode, message: String) : super(message) {
        this.code = code
    }
}
