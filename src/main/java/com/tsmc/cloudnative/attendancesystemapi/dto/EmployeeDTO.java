package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class EmployeeDTO {
    private Integer employeeId;
    private String employeeCode;
    private String employeeName;
    private List<String> roles; // 只保留 role name

    public EmployeeDTO(Integer employeeId, String employeeCode, String employeeName, List<String> roles) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "EmployeeDTO(" +
                "employeeId=" + employeeId +
                ", employeeCode=" + employeeCode +
                ", employeeName=" + employeeName +
                ", roles=" + roles +
                ')';
    }
}

