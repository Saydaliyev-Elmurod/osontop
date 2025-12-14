package com.example.test.exception.handler

import com.example.test.exception.InvalidArgumentException

object Validator {
    fun notNull(errorCode: ErrorCode, `object`: Any?, message: String) {
        if (`object` == null) {
            throw InvalidArgumentException(errorCode, message)
        }
    }

    fun isTrue(errorCode: ErrorCode, expression: Boolean, message: String) {
        if (!expression) {
            throw InvalidArgumentException(errorCode, message)
        }
    }
}
