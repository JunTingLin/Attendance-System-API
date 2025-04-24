package com.tsmc.cloudnative.attendancesystemapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveApplicationRequestDTO {

    @NotNull
    private Integer leaveTypeId;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDatetime;

    @NotNull
    private Integer leaveHours;

    private String reason;

    private String proxyEmployeeCode;

    private String filePath;

    private String fileName;

}
