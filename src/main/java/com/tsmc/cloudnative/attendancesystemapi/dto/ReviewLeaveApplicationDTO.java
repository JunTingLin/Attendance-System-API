package com.tsmc.cloudnative.attendancesystemapi.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewLeaveApplicationDTO {
    @NotBlank(message = "approvalReason cannot be empty")
    private String approvalReason;

    public String getApprovalReason() {
        return approvalReason;
    }
}