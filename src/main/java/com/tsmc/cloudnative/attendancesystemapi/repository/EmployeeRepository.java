package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    // 查詢同部門、同職級的員工作為代理人選項（排除自己）
    // 這裡假如覺得Spring Data JPA的方法命名太長要寫SQL @Query 也是沒問題
    List<Employee> findByDepartmentDepartmentIdAndPositionPositionLevelAndEmployeeIdNotOrderByEmployeeName(
            Integer departmentId, Integer positionLevel, Integer employeeId);
}
