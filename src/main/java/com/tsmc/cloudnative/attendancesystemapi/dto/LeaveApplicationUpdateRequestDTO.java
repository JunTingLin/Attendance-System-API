package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@AllArgsConstructor
public class LeaveApplicationUpdateRequestDTO {
    private Integer leaveTypeId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer leaveHours;
    private String reason;
    private Integer proxyEmployeeId;
    private String filePath;
    private String fileName;
    private String status;
}
