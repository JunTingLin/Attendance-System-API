package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplicationListDTO {
    private Integer applicationId;
    private String leaveTypeName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer leaveHours;
    private String status;
    private LocalDateTime applicationDateTime;
}