package com.example.test.exception.handler

import com.example.test.exception.*
import org.apache.logging.log4j.LogManager
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import tools.jackson.module.kotlin.KotlinInvalidNullException
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@ControllerAdvice
class GlobalExceptionHandler(private val messageSource: ResourceBundleMessageSource) {

  companion object {
    private val LOGGER = LogManager.getLogger()
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleException(e: MethodArgumentNotValidException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, ErrorCode.REQUIRED_FIELD_MISSED)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleException(e: HttpMessageNotReadableException, request: ServerWebExchange): ResponseEntity<*> {
    if (e.cause?.cause is InvalidArgumentException) {
      val cause = e.cause?.cause as InvalidArgumentException
      return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, cause.code)
    }
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, ErrorCode.REQUIRED_FIELD_MISSED)
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception::class, RuntimeException::class)
  fun handleException(e: Exception, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR_CODE)
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(IOException::class)
  fun handleIOException(e: IOException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.IO_EXCEPTION)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidOperationException::class)
  fun handleBadRequests(e: InvalidOperationException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, e.code)
  }

  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  @ExceptionHandler(MaxUploadSizeExceededException::class)
  fun handleBadRequests(e: MaxUploadSizeExceededException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.PAYLOAD_TOO_LARGE, ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidArgumentException::class)
  fun handleBadRequests(e: InvalidArgumentException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, e.code)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun handleBadRequests(e: MethodArgumentTypeMismatchException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_TYPE)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ApiException::class)
  fun handleBadRequests(e: ApiException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, e.code)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(
    DateTimeParseException::class,
    UnsupportedOperationException::class,
    IllegalArgumentException::class,
    IllegalStateException::class,
    NullPointerException::class
  )
  fun handleBadRequests(e: RuntimeException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST_CODE)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException::class)
  fun handleBadRequests(e: BadRequestException, request: ServerWebExchange): ResponseEntity<*> {
    if (e.exceptionResponse != null) {
      return ResponseEntity(e.exceptionResponse, HttpStatus.BAD_REQUEST)
    }
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, e.code)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(AlreadyExistsException::class)
  fun handleBadRequests(e: AlreadyExistsException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, e.code)
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(
    ForbiddenException::class,
    CreateForbiddenException::class,
    DeleteForbiddenException::class,
    ListForbiddenException::class,
    RetrieveForbiddenException::class,
    UpdateForbiddenException::class,
    UserBlockedException::class
  )
  fun handleForbiddenException(e: ForbiddenException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.FORBIDDEN, e.code)
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(JsonParsingException::class, DecodingException::class)
  fun handleJsonException(e: Exception, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.JSON_PARSING_ERROR_CODE)
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException::class)
  fun handleNotFoundException(e: NotFoundException, request: ServerWebExchange): ResponseEntity<*> {
    if (e.exceptionResponse != null) {
      return ResponseEntity(e.exceptionResponse, HttpStatus.NOT_FOUND)
    }
    return constructExceptionResponse(e, request, HttpStatus.NOT_FOUND, e.code)
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException::class)
  fun handleNotFoundException(e: NoSuchElementException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_ERROR_CODE)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ServerWebInputException::class)
  fun handleNullValueException(e: ServerWebInputException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, ErrorCode.REQUIRED_FIELD_MISSED)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(KotlinInvalidNullException::class)
  fun handleNullValueException(e: KotlinInvalidNullException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.BAD_REQUEST, ErrorCode.REQUIRED_FIELD_MISSED)
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNotFoundException(e: NoResourceFoundException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.NOT_FOUND, ErrorCode.URL_NOT_FOUND)
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(
    InvalidCredentialsException::class,
    InvalidTokenException::class,
    UnauthorizedException::class
  )
  fun handleUnauthorizedException(e: UnauthorizedException, request: ServerWebExchange): ResponseEntity<*> {
    if (e.exceptionResponse != null) {
      return ResponseEntity(e.exceptionResponse, HttpStatus.UNAUTHORIZED)
    }
    return constructExceptionResponse(e, request, HttpStatus.UNAUTHORIZED, e.code)
  }


  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ExceptionHandler(ServiceUnavailableException::class)
  fun handleServiceUnavailableException(e: ServiceUnavailableException, request: ServerWebExchange): ResponseEntity<*> {
    return constructExceptionResponse(e, request, HttpStatus.SERVICE_UNAVAILABLE, e.code)
  }

  private fun constructExceptionResponse(
    e: Exception,
    request: ServerWebExchange,
    status: HttpStatus,
    errorCode: ErrorCode
  ): ResponseEntity<ExceptionResponse> {
    val path = request.request.path.value()
    LOGGER.error("Failed to request [{}] path. Error:", path, e)
    val language: String = request.request.headers.acceptLanguage.firstOrNull()?.range ?: "uz";
    var exc: Throwable = e
    while (exc.cause != null) {
      exc = exc.cause!!
    }
    val msg = messageSource.getMessage(errorCode.name, null, e.message, Locale.forLanguageTag(language))
    val exceptionResponse = ExceptionResponse(
      code = errorCode,
      status = "${status.value()} ${status.reasonPhrase}",
      path = path,
      errorMessage = exc.message,
      errorDescription = msg,
      timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
    )
    return ResponseEntity(exceptionResponse, status)
  }
}
