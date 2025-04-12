package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee findEmployeeByCode(String employeeCode) {
        return employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("員工不存在"));
    }
    private EmployeeDTO convertToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getEmployeeName(),
                employee.getEmployeeRoles().stream()
                        .map(er -> er.getRole().getName())
                        .collect(Collectors.toList())
        );
    }

    public EmployeeDTO getEmployeeDTOByCode(String employeeCode) {
        Employee employee = findEmployeeByCode(employeeCode);
        log.debug("Employee: " + employee.getEmployeeName());
        log.debug("EmployeeRoles size: " + employee.getEmployeeRoles().size());
        employee.getEmployeeRoles().forEach(er -> {
            log.debug("Role: " + (er.getRole() != null ? er.getRole().getName() : "null"));
        });
        return convertToDTO(employee);
    }


}

