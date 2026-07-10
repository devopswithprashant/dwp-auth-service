package com.devopswithprashant.service.auth.common.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handles validation errors`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "target")
        bindingResult.addError(FieldError("target", "username", "Username is required"))
        val parameter = org.springframework.core.MethodParameter(
            GlobalExceptionHandler::class.java.getDeclaredMethod("handleValidationException", MethodArgumentNotValidException::class.java),
            0
        )
        val exception = MethodArgumentNotValidException(parameter, bindingResult)

        val response = handler.handleValidationException(exception)

        assertEquals(400, response.statusCode.value())
        assertEquals("VALIDATION-001", response.body?.code)
        assertEquals("Username is required", response.body?.message)
    }

    @Test
    fun `handles business exceptions`() {
        val response = handler.handleBusinessException(BusinessException(AuthErrorCode.ACCOUNT_LOCKED))

        assertEquals(403, response.statusCode.value())
        assertEquals(AuthErrorCode.ACCOUNT_LOCKED.code, response.body?.code)
        assertEquals(AuthErrorCode.ACCOUNT_LOCKED.message, response.body?.message)
    }

    @Test
    fun `handles unexpected exceptions`() {
        val response = handler.handleUnexpectedException(RuntimeException("boom"))

        assertEquals(500, response.statusCode.value())
        assertEquals("AUTH-999", response.body?.code)
    }
}
