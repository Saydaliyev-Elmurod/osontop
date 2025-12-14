package com.example.test.exception

import com.example.test.exception.handler.ErrorCode

class InvalidTokenException : UnauthorizedException() {
    override var code = ErrorCode.INVALID_TOKEN_ERROR_CODE
}
