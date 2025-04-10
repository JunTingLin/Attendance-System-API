package com.tsmc.cloudnative.attendancesystemapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "t_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "position_id", nullable = false)
    private Integer positionId;

    @Column(name = "supervisor_id")
    private Integer supervisorId;

    @Column(name = "hire_date", nullable = false)
    private Date hireDate;

    @Column(name = "years_of_service", nullable = false)
    private Integer yearsOfService;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // 防止無限循環發生
    @EqualsAndHashCode.Exclude // 避免 equals 與 hashCode 遞迴
    private Set<EmployeeRole> employeeRoles = new HashSet<>();
}