package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationListDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
@Slf4j
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;


    @GetMapping
    @Operation(summary = "查詢個人所有請假歷史紀錄列表（list）")
    public ApiResponse<List<LeaveApplicationListDTO>> getEmployeeLeaveApplications(Authentication authentication) {
        String employeeCode = authentication.getName();
        log.info("用戶[{}]查詢其所有請假記錄", employeeCode);

        List<LeaveApplicationListDTO> applications = leaveApplicationService.getEmployeeLeaveApplications(employeeCode);

        return ApiResponse.success("成功獲取請假記錄列表", applications);
    }


    @GetMapping("/{id}")
    @Operation(summary = "查詢個人單筆請假紀錄（詳情）")
    public ApiResponse<LeaveApplicationResponseDTO> getEmployeeLeaveApplicationDetail(
            Authentication authentication,
            @PathVariable("id") Integer applicationId) {
        String employeeCode = authentication.getName();
        log.info("用戶[{}]查詢其請假記錄[{}]詳情", employeeCode, applicationId);

        LeaveApplicationResponseDTO application = leaveApplicationService
                .getEmployeeLeaveApplicationDetail(employeeCode, applicationId);

        return ApiResponse.success("成功獲取請假記錄詳情", application);
    }
}