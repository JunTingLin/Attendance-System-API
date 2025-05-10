package com.tsmc.cloudnative.attendancesystemapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplicationRequestDTO {

    @NotNull(message = "請假類型不能為空")
    private Integer leaveTypeId;

    @NotNull(message = "開始時間不能為空")
    private LocalDateTime startDateTime;

    @NotNull(message = "結束時間不能為空")
    private LocalDateTime endDateTime;

    @NotNull(message = "請假時數不能為空")
    private Integer leaveHours;

    private String reason;

    private String proxyEmployeeCode;

    private String filePath;

    private String fileName;

    private String status;
}
