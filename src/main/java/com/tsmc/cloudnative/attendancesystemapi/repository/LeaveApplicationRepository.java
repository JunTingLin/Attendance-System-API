package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    // 查詢特定員工的所有請假記錄（按申請時間降序排列）
    List<LeaveApplication> findByEmployeeEmployeeIdOrderByApplicationDatetimeDesc(Integer employeeId);

    // 查詢特定員工的特定請假記錄（確保權限控制）
    Optional<LeaveApplication> findByApplicationIdAndEmployeeEmployeeId(Integer applicationId, Integer employeeId);
}
