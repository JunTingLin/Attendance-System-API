package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor  // 使用建構子注入
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "使用 employee code/pwd 登入並回傳 JWT")
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.authenticateAndGenerateToken(loginRequestDTO);
        return ApiResponse.success("登入成功", response);
    }

    @GetMapping("/info")
    @Operation(summary = "查詢目前員工的基本資料")
    public ApiResponse<LoginResponseDTO> info(@AuthenticationPrincipal UserDetails userDetails) {
        LoginResponseDTO loginInfo = authService.getAuthenticatedUserInfo(userDetails);
        return ApiResponse.success("取得登入資訊成功", loginInfo);
    }
}