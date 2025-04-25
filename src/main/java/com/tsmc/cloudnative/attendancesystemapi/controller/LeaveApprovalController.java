package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveApplicationService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.ReviewLeaveApplicationDTO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/manager/leaves")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
@Slf4j
public class LeaveApprovalController {

    private final LeaveApplicationService leaveApplicationService;

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
