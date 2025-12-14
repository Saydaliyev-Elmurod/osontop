package com.example.test.exception

import com.example.test.exception.handler.ErrorCode
import com.example.test.exception.handler.ExceptionInterface

class LockingConflictException : RuntimeException, ExceptionInterface {
    override val code = ErrorCode.LOCKING_CONFLICT_ERROR_CODE

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor(
        message: String,
        cause: Throwable,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace)
}
