package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeLeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeLeaveBalanceRepository extends JpaRepository<EmployeeLeaveBalance, Integer> {

     // 根據員工ID查詢所有請假餘額 (按照假別ID排序) //目前沒用到
    List<EmployeeLeaveBalance> findByEmployeeEmployeeIdOrderByLeaveTypeLeaveTypeId(Integer employeeId);

    // 根據員工ID和年度查詢所有請假餘額 (按照假別ID排序)
    List<EmployeeLeaveBalance> findByEmployeeEmployeeIdAndLeaveYearOrderByLeaveTypeLeaveTypeId(
            Integer employeeId, Integer leaveYear);

    // 根據員工ID和假別ID查詢該年度該假別剩餘小時數
    Optional<EmployeeLeaveBalance> findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
        Integer employeeId, Integer leaveTypeId, Integer leaveYear);

}