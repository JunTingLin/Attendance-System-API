package com.tsmc.cloudnative.attendancesystemapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewLeaveApplicationDTO {
    @NotBlank(message = "approvalReason cannot be empty")
    private String approvalReason;

    public String getApprovalReason() {
        return approvalReason;
    }
}