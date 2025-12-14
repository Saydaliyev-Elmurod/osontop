package com.example.test.exception

import com.example.test.exception.handler.ErrorCode

class CreateForbiddenException : ForbiddenException {
    override var code = ErrorCode.CREATE_FORBIDDEN_ERROR_CODE

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(code: ErrorCode, message: String) : super(message) {
        this.code = code
    }
    constructor(code: ErrorCode) : super() {
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
