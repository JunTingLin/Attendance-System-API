package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.common.LeaveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveApprovalService {

    private final LeaveApplicationRepository leaveApplicationRepository;

    public ApiResponse<Void> approveLeaveApplication(Integer leaveId, String approvalReason) {
        return updateLeaveApplicationStatus(leaveId, "已核准", approvalReason, "approved");
    }

    public ApiResponse<Void> rejectLeaveApplication(Integer leaveId, String approvalReason) {
        return updateLeaveApplicationStatus(leaveId, "已駁回", approvalReason, "rejected");
    }

    private ApiResponse<Void> updateLeaveApplicationStatus(Integer leaveId, String status, String approvalReason, String action) {
        try {
            Optional<LeaveApplication> leaveApplicationOptional = leaveApplicationRepository.findById(leaveId);

            if (!leaveApplicationRepository.existsById(leaveId)) {
                log.error("沒有找到請假申請：{} ", leaveId);
                return ApiResponse.error(404, "Leave application with leaveId:" + leaveId + " not found.");
            }
            LeaveApplication leaveApplication = leaveApplicationOptional.get();
            leaveApplication.setStatus(status);
            leaveApplication.setApprovalReason(approvalReason);
            leaveApplicationRepository.save(leaveApplication);
            log.info("請假申請：{} 已成功 {}", leaveId, action);
            return ApiResponse.success("Leave application " + action + ".", null);
        } catch (DataAccessException e) {
            log.error("資料庫存取錯誤：{} ", e.getMessage());
            return ApiResponse.error(500, "Database access error: " + e.getMessage());
        }
    }
}