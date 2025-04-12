package com.tsmc.cloudnative.attendancesystemapi.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Object> errorResponse = ApiResponse.error(
                HttpStatus.FORBIDDEN.value(),
                "權限不足：" + accessDeniedException.getMessage()
        );

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
