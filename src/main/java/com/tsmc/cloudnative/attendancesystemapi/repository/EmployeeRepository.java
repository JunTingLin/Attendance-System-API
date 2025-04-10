package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmployeeCode(String employeeCode);
}
