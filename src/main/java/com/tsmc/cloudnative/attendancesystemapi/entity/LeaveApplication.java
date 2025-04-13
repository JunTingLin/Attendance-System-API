package com.tsmc.cloudnative.attendancesystemapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_leave_application")
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "leave_hours", nullable = false)
    private Integer leaveHours;

    @Column(name = "reason", nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "proxy_employee_id", nullable = false)
    private Employee proxyEmployee;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "application_datetime", nullable = false)
    private LocalDateTime applicationDatetime;

    @ManyToOne
    @JoinColumn(name = "approver_employee_id")
    private Employee approverEmployee;

    @Column(name = "approval_reason")
    private String approvalReason;

    @Column(name = "approval_datetime")
    private LocalDateTime approvalDatetime;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;
}