package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@AllArgsConstructor
public class LeaveApplicationResponseDTO {
    private Integer leaveApplicationId;
    private Integer employeeId;
    private String employeeName;
    private Integer leaveTypeId;
    private String leaveTypeName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer leaveHours;
    private String reason;
    private Integer proxyEmployeeId;
    private String proxyEmployeeName;
    private String status;
    private LocalDateTime applicationDateTime;
    private Integer approverEmployeeId;
    private String approverEmployeeName;
    private String approvalReason;
    private String filePath;
    private String fileName;
}
