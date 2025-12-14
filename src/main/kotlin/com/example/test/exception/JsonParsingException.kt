package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class JsonParsingException : RuntimeException, ExceptionInterface {
    override var code: ErrorCode = ErrorCode.JSON_PARSING_ERROR_CODE

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(errorCode: ErrorCode, message: String) : super(message) {
        this.code = errorCode
    }
    constructor(cause: Throwable) : super(cause)
}
