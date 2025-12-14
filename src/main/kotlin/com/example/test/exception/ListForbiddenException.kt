package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class ListForbiddenException : ForbiddenException, ExceptionInterface {
    override var code = ErrorCode.LIST_FORBIDDEN_ERROR_CODE

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
