package com.tsmc.cloudnative.attendancesystemapi.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveApprovalService;
import com.tsmc.cloudnative.attendancesystemapi.dto.ReviewLeaveApplicationDTO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/manager/leaves")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
@Slf4j
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;

    @PutMapping("/{leaveId}/approve")
    @Operation(summary = "核准價單")
    public ApiResponse<Void> approveLeave(@PathVariable Integer leaveId, @Valid @RequestBody ReviewLeaveApplicationDTO request) {
        log.info("核准假單 {}", leaveId);
        return leaveApprovalService.approveLeaveApplication(leaveId, request.getApprovalReason());
    }

    @PutMapping("/{leaveId}/reject")
    @Operation(summary = "駁回假單")
    public ApiResponse<Void> rejectLeave(@PathVariable Integer leaveId, @Valid @RequestBody ReviewLeaveApplicationDTO request) {
        log.info("駁回假單 {}", leaveId);
        return leaveApprovalService.rejectLeaveApplication(leaveId, request.getApprovalReason());
    }
}
