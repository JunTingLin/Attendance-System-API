package com.tsmc.cloudnative.attendancesystemapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_employee_leave_balance")
public class EmployeeLeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Integer balanceId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "leave_year", nullable = false)
    private Integer leave_year;

    @Column(name = "total_hours", nullable = false)
    private Integer totalHours;

    @Column(name = "used_hours", nullable = false)
    private Integer usedHours;

    @Column(name = "remaining_hours", nullable = false)
    private Integer remainingHours;
}