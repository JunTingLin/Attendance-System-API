package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceDTO {
    private Integer balanceId;
    private Integer employeeId;
    private String employeeName;
    private Integer leaveTypeId;
    private String leaveTypeName;
    private Integer leaveYear;
    private Integer totalHours;
    private Integer usedHours;
    private Integer remainingHours;
}