package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.AuthService;
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
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = authService.authenticateAndGenerateToken(loginRequestDTO);
        return ApiResponse.success("登入成功", response);
    }

    @GetMapping("/info")
    public ApiResponse<LoginResponseDTO> info(@AuthenticationPrincipal UserDetails userDetails) {
        LoginResponseDTO loginInfo = authService.getAuthenticatedUserInfo(userDetails);
        return ApiResponse.success("取得登入資訊成功", loginInfo);
    }
}