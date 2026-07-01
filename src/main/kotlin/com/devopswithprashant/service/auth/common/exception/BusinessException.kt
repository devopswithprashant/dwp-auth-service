package com.devopswithprashant.service.auth.common.exception

import com.devopswithprashant.service.auth.exception.AuthErrorCode

class BusinessException(

    val errorCode: AuthErrorCode

) : RuntimeException(errorCode.message)