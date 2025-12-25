package com.example.test.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified T> logger(): Logger? =
  LoggerFactory.getLogger(T::class.java)

fun logger(name: String, block: Logger.() -> Unit) =
  LoggerFactory.getLogger(name)?.also(block)

class Extensions {

}
