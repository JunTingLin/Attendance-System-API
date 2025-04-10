package com.tsmc.cloudnative.attendancesystemapi.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "t_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "name", nullable = false)
    private String name;
}
