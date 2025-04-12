package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LoginResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor  // 使用建構子注入
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO response = authService.authenticateAndGenerateToken(loginRequestDTO);
            return ApiResponse.success("登入成功", response);
        }
        catch (AuthenticationException e) {
            return ApiResponse.error(401, "登入失敗：" + e.getMessage());
        }
        catch (Exception e) {
            // 所有其他業務邏輯錯誤都作為 500 處理
            return ApiResponse.error(500, "取得登入資訊失敗：" + e.getMessage());
        }

    }
    @GetMapping("/info")
    public ApiResponse<LoginResponseDTO> info(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            LoginResponseDTO loginInfo = authService.getAuthenticatedUserInfo(userDetails);
            return ApiResponse.success("取得登入資訊成功", loginInfo);
        } catch (Exception e) {
            return ApiResponse.error(500, "取得登入資訊失敗：" + e.getMessage());
        }
    }

}