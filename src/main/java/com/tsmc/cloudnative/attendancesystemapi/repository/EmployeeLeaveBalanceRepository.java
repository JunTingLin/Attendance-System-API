package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeLeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeLeaveBalanceRepository extends JpaRepository<EmployeeLeaveBalance, Integer> {

     // 根據員工ID查詢所有請假餘額 (按照假別ID排序) //目前沒用到
    List<EmployeeLeaveBalance> findByEmployeeEmployeeIdOrderByLeaveTypeLeaveTypeId(Integer employeeId);

    // 根據員工ID和年度查詢所有請假餘額 (按照假別ID排序)
    List<EmployeeLeaveBalance> findByEmployeeEmployeeIdAndLeaveYearOrderByLeaveTypeLeaveTypeId(
            Integer employeeId, Integer leaveYear);
}