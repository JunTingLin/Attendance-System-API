package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    // 查詢特定員工的所有請假記錄（按申請時間降序排列）
    List<LeaveApplication> findByEmployeeEmployeeIdOrderByApplicationDatetimeDesc(Integer employeeId);

    // 查詢特定員工的特定請假記錄（確保權限控制）
    Optional<LeaveApplication> findByApplicationIdAndEmployeeEmployeeId(Integer applicationId, Integer employeeId);

    // 檢查檔案是否屬於特定員工（檔案權限控制）
    boolean existsByFilePathAndEmployeeEmployeeCode(String filePath, String employeeCode);

    // 查詢特定主管下屬的所有請假記錄（按申請時間降序排列）
    List<LeaveApplication> findByEmployeeSupervisorEmployeeIdOrderByApplicationDatetimeDesc(Integer supervisorId);

    // 查詢特定主管下屬的特定請假記錄
    Optional<LeaveApplication> findByApplicationIdAndEmployeeSupervisorEmployeeId(Integer applicationId, Integer supervisorId);
}
