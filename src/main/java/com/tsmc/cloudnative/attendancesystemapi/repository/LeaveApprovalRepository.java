package com.tsmc.cloudnative.attendancesystemapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;

@Repository
public interface LeaveApprovalRepository extends JpaRepository<LeaveApplication, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE LeaveApplication la SET la.status = ?2, la.approvalReason = ?3 WHERE la.id = ?1")
    void updateStatusAndApprovalReasonById(Integer leaveId, String status, String approvalReason);
}
