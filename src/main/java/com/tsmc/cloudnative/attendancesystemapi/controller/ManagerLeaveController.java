package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationListDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.ReviewLeaveApplicationDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leaves")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('MANAGER')")
public class ManagerLeaveController {

    private final LeaveApplicationService leaveApplicationService;

    @GetMapping
    @Operation(summary = "查詢主管底下所有員工的假單")
    public ApiResponse<List<LeaveApplicationListDTO>> getSubordinatesLeaveApplications(Authentication authentication) {
        String managerCode = authentication.getName();
        log.info("主管[{}]查詢所有下屬的請假記錄", managerCode);

        List<LeaveApplicationListDTO> applications = leaveApplicationService.getManagerSubordinatesLeaveApplications(managerCode);

        return ApiResponse.success("成功獲取下屬請假記錄列表", applications);
    }


    @GetMapping("/{id}")
    @Operation(summary = "查詢特定假單詳細內容")
    public ApiResponse<LeaveApplicationResponseDTO> getLeaveApplicationDetail(
            Authentication authentication,
            @PathVariable("id") Integer applicationId) {
        String managerCode = authentication.getName();
        log.info("主管[{}]查詢請假記錄[{}]詳情", managerCode, applicationId);

        LeaveApplicationResponseDTO application = leaveApplicationService
                .getManagerLeaveApplicationDetail(managerCode, applicationId);

        return ApiResponse.success("成功獲取請假記錄詳情", application);
    }

    @PutMapping("/{leaveId}/approve")
    @Operation(summary = "核准價單")
    public ApiResponse<LeaveApplicationResponseDTO> approveLeave(@PathVariable Integer leaveId, @Valid @RequestBody ReviewLeaveApplicationDTO request) {
        log.info("核准假單 {}", leaveId);
        LeaveApplicationResponseDTO response = leaveApplicationService.approveLeaveApplication(leaveId, request.getApprovalReason());
        return ApiResponse.success("Leave application approved successfully.", response);
    }

    @PutMapping("/{leaveId}/reject")
    @Operation(summary = "駁回假單")
    public ApiResponse<LeaveApplicationResponseDTO> rejectLeave(@PathVariable Integer leaveId, @Valid @RequestBody ReviewLeaveApplicationDTO request) {
        log.info("駁回假單 {}", leaveId);
        LeaveApplicationResponseDTO response = leaveApplicationService.rejectLeaveApplication(leaveId, request.getApprovalReason());
        return ApiResponse.success("Leave application rejected successfully.", response);
    }
}