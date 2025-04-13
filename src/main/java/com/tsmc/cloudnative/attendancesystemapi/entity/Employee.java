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

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Employee supervisor;

    @OneToMany(mappedBy = "supervisor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Employee> subordinates = new HashSet<>();

    @Column(name = "hire_date", nullable = false)
    private Date hireDate;

    @Column(name = "months_of_service", nullable = false)
    private Integer monthsOfService;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<EmployeeRole> employeeRoles = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @ToString.Exclude // 防止無限循環發生
    @EqualsAndHashCode.Exclude  // 避免 equals 與 hashCode 遞迴
    private Set<EmployeeLeaveBalance> leaveBalances = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LeaveApplication> leaveApplications = new HashSet<>();

    @OneToMany(mappedBy = "proxyEmployee")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LeaveApplication> proxyLeaveApplications = new HashSet<>();

    @OneToMany(mappedBy = "approverEmployee")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LeaveApplication> approvedLeaveApplications = new HashSet<>();
}