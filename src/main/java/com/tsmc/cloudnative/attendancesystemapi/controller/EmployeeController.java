package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "查詢目前員工詳細資料")
    public ApiResponse<EmployeeDTO> getLoggedInEmployee(Authentication authentication) {
        String employeeCode = authentication.getName();
        EmployeeDTO dto = employeeService.getEmployeeDTOByCode(employeeCode);
        return ApiResponse.success("成功取得用戶資料", dto);
    }

    @GetMapping("/proxies")
    @Operation(summary = "查詢同部門同層級之代理人員工資訊")
    public ApiResponse<List<EmployeeDTO>> getPotentialProxies(Authentication authentication) {
        String employeeCode = authentication.getName();
        log.info("用戶[{}]查詢可選代理人列表", employeeCode);

        List<EmployeeDTO> proxies = employeeService.getPotentialProxies(employeeCode);

        return ApiResponse.success("成功獲取可選代理人列表", proxies);
    }

}


