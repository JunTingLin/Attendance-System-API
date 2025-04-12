package com.tsmc.cloudnative.attendancesystemapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String message = "未經授權的訪問";
        if (authException != null) {
            message += ": " + authException.getMessage();
        }

        ApiResponse<Object> errorResponse = ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, message);
        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
