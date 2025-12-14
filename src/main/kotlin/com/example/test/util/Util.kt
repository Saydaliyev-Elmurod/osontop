package com.example.test.util

import com.example.test.exception.NotFoundException
import com.example.test.exception.handler.ErrorCode
import lombok.extern.log4j.Log4j2
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.math.log


@Log4j2
object Util {
  fun getTimeStampFromInstant(instant: Instant): Timestamp {
    val ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    return Timestamp.valueOf(ldt)
  }

  fun <T> checkNull(t: T?, tClass: Class<T?>) {
    if (t == null) {
      throw NotFoundException(tClass.getSimpleName() + " not found")
    }
  }

  fun <T> checkNull(errorCode: ErrorCode, t: T?, tClass: Class<T?>) {
    if (t == null) {
      throw NotFoundException(code = errorCode, message = tClass.getSimpleName() + " not found")
    }
  }

  fun <T> mapById(list: MutableList<T?>, idExtractor: Function<T?, UUID?>): HashMap<UUID?, T?> {
    val map = HashMap<UUID?, T?>()
    list.forEach(Consumer { item: T? -> map[idExtractor.apply(item)] = item })
    return map
  }

  fun <T> mapByIdString(
    list: MutableList<T?>, idExtractor: Function<T?, String?>
  ): HashMap<String?, T?> {
    val map = HashMap<String?, T?>()
    list.forEach(Consumer { item: T? -> map[idExtractor.apply(item)] = item })
    return map
  }

  fun <T> idList(list: MutableList<T?>, idExtractor: Function<T?, UUID?>?): MutableList<UUID?> {
    return list.stream().map<UUID?>(idExtractor).collect(Collectors.toSet()).stream().toList()
  }

  fun <T> idSet(list: MutableList<T?>, idExtractor: Function<T?, UUID?>?): MutableSet<UUID?> {
    return list.stream().map<UUID?>(idExtractor).collect(Collectors.toSet())
  }

  fun <T> idListStr(list: MutableList<T?>, idExtractor: Function<T?, String?>?): MutableList<String?> {
    return list.stream().map<String?>(idExtractor).collect(Collectors.toSet()).stream().toList()
  }


  fun <E : Enum<E?>?> nameEnums(enums: MutableCollection<E?>): MutableList<String?> {
    return enums.stream().map<String?> { obj: E? -> obj!!.name }.toList()
  }

  fun <T : String?> getNotNull(t1: T?, t2: T?): T? {
    if (t1.isNullOrEmpty()) return t2
    return t1
  }

  fun getEmptyStr(str: String?): String {
    if (str == null) return ""
    return str
  }

  fun <T> getNotNull(t1: T?, t2: T?): T? {
    if (t1 == null) return t2
    return t1
  }

  fun <T> getNotNull(t1: T?, t2: T?, t3: T?): T? {
    if (t1 == null) return Util.getNotNull<T?>(t2, t3)
    return t1
  }

  fun <E> isEmpty(elements: MutableCollection<E?>?): Boolean {
    return elements.isNullOrEmpty()
  }

  fun <E> isNotEmpty(elements: MutableCollection<E?>?): Boolean {
    return !elements.isNullOrEmpty()
  }

  fun isEmpty(str: String?): Boolean {
    return str.isNullOrEmpty()
  }

}