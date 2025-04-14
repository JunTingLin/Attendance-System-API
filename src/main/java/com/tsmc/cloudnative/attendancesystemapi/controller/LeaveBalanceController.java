package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveBalanceDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;


    @GetMapping
    public ApiResponse<List<LeaveBalanceDTO>> getEmployeeLeaveBalances(Authentication authentication) {
        String employeeCode = authentication.getName();
        log.info("用戶[{}]查詢其當前年度的所有請假餘額", employeeCode);

        List<LeaveBalanceDTO> balances = leaveBalanceService.getEmployeeLeaveBalances(employeeCode);

        return ApiResponse.success("成功獲取請假餘額列表", balances);
    }


    @GetMapping("/{year}")
    public ApiResponse<List<LeaveBalanceDTO>> getEmployeeLeaveBalancesForYear(
            Authentication authentication,
            @PathVariable Integer year) {
        String employeeCode = authentication.getName();
        log.info("用戶[{}]查詢其{}年度的所有請假餘額", employeeCode, year);

        // 簡單的年份驗證
        int currentYear = LocalDate.now().getYear();
        if (year < 2000 || year > currentYear + 1) {
            return ApiResponse.error(400, "無效的年份");
        }

        List<LeaveBalanceDTO> balances = leaveBalanceService.getEmployeeLeaveBalancesForYear(employeeCode, year);

        return ApiResponse.success("成功獲取請假餘額列表", balances);
    }
}