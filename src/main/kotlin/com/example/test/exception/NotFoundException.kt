package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface
import java.util.NoSuchElementException

class NotFoundException : NoSuchElementException, ExceptionInterface {
    override var code: ErrorCode = ErrorCode.NOT_FOUND_ERROR_CODE
    var exceptionResponse: com.example.test.exception.handler.ExceptionResponse? = null

    constructor(code: ErrorCode) : super() {
        this.code = code
    }

    constructor(exceptionResponse: com.example.test.exception.handler.ExceptionResponse) : super() {
        this.exceptionResponse = exceptionResponse
    }

    constructor(message: String) : super(message)

    constructor(code: ErrorCode, message: String) : super(message) {
        this.code = code
    }
}
