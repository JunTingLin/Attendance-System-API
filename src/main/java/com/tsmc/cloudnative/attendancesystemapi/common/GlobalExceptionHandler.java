package com.tsmc.cloudnative.attendancesystemapi.common;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleException(Exception e) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleAuthenticationException(AuthenticationException e) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "驗證失敗：" + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "權限不足：" + e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleBadCredentialsException(BadCredentialsException e) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "帳號或密碼錯誤");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    // 可以添加其他自定義業務異常處理
    // 例如：
    // @ExceptionHandler(BusinessException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public ApiResponse<Object> handleBusinessException(BusinessException e) {
    //     return ApiResponse.error(e.getCode(), e.getMessage());
    // }
}
