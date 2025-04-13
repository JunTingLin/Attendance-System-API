package com.tsmc.cloudnative.attendancesystemapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_leave_rules")
public class LeaveRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Integer ruleId;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "months_of_service_min", nullable = false)
    private Integer monthsOfServiceMin;

    @Column(name = "months_of_service_max")
    private Integer monthsOfServiceMax;

    @Column(name = "hours_entitled", nullable = false)
    private Integer hoursEntitled;
}