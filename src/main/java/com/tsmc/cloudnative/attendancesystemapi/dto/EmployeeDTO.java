package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
public class EmployeeDTO {
    private Integer employeeId;
    private String employeeCode;
    private String employeeName;
    private List<Integer> roleIds;
    private List<String> roleNames;
    private Integer departmentId;
    private String DepartmentName;
    private Integer positionId;
    private String PositionName;
    private String supervisorEmployeeCode;
    private String supervisorEmployeeName;
    private LocalDate hireDate;
    private Integer monthsOfService;


}

