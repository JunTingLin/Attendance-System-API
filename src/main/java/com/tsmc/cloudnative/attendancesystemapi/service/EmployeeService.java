package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public List<EmployeeDTO> getPotentialProxies(String employeeCode) {
        log.debug("開始查詢員工[{}]的潛在代理人列表", employeeCode);

        Employee employee = findEmployeeByCode(employeeCode);
        Integer departmentId = employee.getDepartment().getDepartmentId();
        Integer employeeId = employee.getEmployeeId();

        log.debug("員工部門ID: {}", departmentId);

        // 查詢同部門的員工
        List<Employee> potentialProxies = employeeRepository.findByDepartmentDepartmentIdAndEmployeeIdNotOrderByEmployeeName(
                departmentId, employeeId);

        log.debug("找到 {} 位潛在代理人", potentialProxies.size());

        return potentialProxies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        // Use Optional to safely extract supervisor details
        String supervisorCode = Optional.ofNullable(employee.getSupervisor())
                .map(s -> s.getEmployeeCode())
                .orElse(null);
        String supervisorName = Optional.ofNullable(employee.getSupervisor())
                .map(s -> s.getEmployeeName())
                .orElse(null);

        List<Integer> roleIds = employee.getEmployeeRoles().stream()
                .map(er -> er.getRole().getRoleId())
                .collect(Collectors.toList());
        List<String> roleNames = employee.getEmployeeRoles().stream()
                .map(er -> er.getRole().getName())
                .collect(Collectors.toList());

        return new EmployeeDTO(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getEmployeeName(),
                roleIds,
                roleNames,
                employee.getDepartment().getDepartmentId(),
                employee.getDepartment().getDepartmentName(),
                employee.getPosition().getPositionId(),
                employee.getPosition().getPositionName(),
                supervisorCode,
                supervisorName,
                employee.getHireDate(),
                employee.getMonthsOfService()
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

