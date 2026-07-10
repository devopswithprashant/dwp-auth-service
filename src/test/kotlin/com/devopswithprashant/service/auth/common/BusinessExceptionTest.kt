package com.devopswithprashant.service.auth.common

import com.devopswithprashant.service.auth.common.exception.AuthErrorCode
import com.devopswithprashant.service.auth.common.exception.BusinessException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BusinessExceptionTest {

    @Test
    fun `business exception exposes error code and message`() {
        val exception = BusinessException(AuthErrorCode.ACCOUNT_DISABLED)

        assertEquals(AuthErrorCode.ACCOUNT_DISABLED, exception.errorCode)
        assertEquals(AuthErrorCode.ACCOUNT_DISABLED.message, exception.message)
    }
}
