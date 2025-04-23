package com.tsmc.cloudnative.attendancesystemapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveType;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer>{

}
