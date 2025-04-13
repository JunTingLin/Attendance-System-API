package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
public class EmployeeDTO {
    private Integer employeeId;
    private String employeeCode;
    private String employeeName;
    private List<String> roles; // 只保留 role name
    private String DepartmentName;
    private String PositionName;
    private String supervisorEmployeeCode;
    private String supervisorEmployeeName;
    private Date hireDate;
    private Integer monthsOfService;


}

