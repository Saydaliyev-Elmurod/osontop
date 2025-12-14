package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class AlreadyExistsException : RuntimeException, ExceptionInterface {
    override var code: ErrorCode = ErrorCode.ALREADY_EXISTS_ERROR_CODE

    constructor(code: ErrorCode) : super() {
        this.code = code
    }

    constructor(message: String) : super(message)

    constructor(code: ErrorCode, message: String) : super(message) {
        this.code = code
    }
}
