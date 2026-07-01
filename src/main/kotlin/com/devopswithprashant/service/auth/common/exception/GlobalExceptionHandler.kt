package com.devopswithprashant.service.auth.common.exception

import com.devopswithprashant.service.auth.common.api.ApiErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ApiErrorResponse> {

        val message = exception.bindingResult
            .fieldErrors
            .firstOrNull()
            ?.defaultMessage
            ?: "Validation failed."

        return ResponseEntity
            .badRequest()
            .body(
                ApiErrorResponse(
                    code = "VALIDATION-001",
                    message = message
                )
            )
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        exception: BusinessException
    ): ResponseEntity<ApiErrorResponse> {

        val errorCode = exception.errorCode

        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(
                ApiErrorResponse(
                    code = errorCode.code,
                    message = errorCode.message
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        exception: Exception
    ): ResponseEntity<ApiErrorResponse> {

        return ResponseEntity
            .internalServerError()
            .body(
                ApiErrorResponse(
                    code = "AUTH-999",
                    message = "Unexpected server error."
                )
            )
    }
}