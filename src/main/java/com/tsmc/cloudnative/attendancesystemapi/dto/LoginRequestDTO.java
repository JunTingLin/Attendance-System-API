package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String employeeCode;
    private String password;
}
