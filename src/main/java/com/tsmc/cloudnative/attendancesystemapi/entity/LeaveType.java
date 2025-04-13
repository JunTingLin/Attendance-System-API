package com.tsmc.cloudnative.attendancesystemapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "t_leave_type")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_type_id")
    private Integer leaveTypeId;

    @Column(name = "leave_type_name", nullable = false)
    private String leaveTypeName;

    @Column(name = "attachment_required", nullable = false)
    private Boolean attachmentRequired;

    @OneToMany(mappedBy = "leaveType")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<EmployeeLeaveBalance> leaveBalances = new HashSet<>();

    @OneToMany(mappedBy = "leaveType")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LeaveApplication> leaveApplications = new HashSet<>();

    @OneToMany(mappedBy = "leaveType")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LeaveRules> leaveRules = new HashSet<>();
}