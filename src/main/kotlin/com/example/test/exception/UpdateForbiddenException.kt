package com.example.test.exception

import com.example.test.exception.handler.ErrorCode

class UpdateForbiddenException : ForbiddenException {
    override var code = ErrorCode.UPDATE_FORBIDDEN_ERROR_CODE

    constructor(code: ErrorCode) : super(code)

    constructor(message: String) : super(message)

    constructor(code: ErrorCode, message: String) : super(code, message) {
        this.code = code
    }
}
