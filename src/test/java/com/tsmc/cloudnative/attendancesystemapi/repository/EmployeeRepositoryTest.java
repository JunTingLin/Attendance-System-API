package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRoleRepository employeeRoleRepository;

    // Assume these repositories exist for setting up required entities
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;


    @BeforeEach
    void setup() {
        Department department = new Department();
        department.setDepartmentName("營運組織");
        department.setDepartmentCode("D001");
        departmentRepository.save(department);

        Position position = new Position();
        position.setPositionName("組織長");
        position.setPositionLevel(4);
        positionRepository.save(position);

        Employee employee = new Employee();
        employee.setEmployeeCode("test123");
        employee.setEmployeeName("Test Employee");
        employee.setPassword("testPass");
        employee.setHireDate(new Date());
        employee.setMonthsOfService(1);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setSupervisor(null);
        employeeRepository.save(employee);

        Role role = new Role();
        role.setName("EMPLOYEE");
        roleRepository.save(role);

        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setEmployee(employee);
        employeeRole.setRole(role);
        employeeRoleRepository.save(employeeRole);

        employee.getEmployeeRoles().add(employeeRole);
        employeeRepository.save(employee);
    }


    @Test
    void testFindByEmployeeCode() {
        Employee employee = employeeRepository.findByEmployeeCode("test123")
                .orElseThrow(() -> new RuntimeException("找不到該員工"));  // 到這裡只會回傳Optional<Employee> 所以要解包 Optional，若找不到資料就拋出異常

        // Use Optional to safely extract supervisor details
        String supervisorCode = Optional.ofNullable(employee.getSupervisor())
                .map(s -> s.getEmployeeCode())
                .orElse(null);
        String supervisorName = Optional.ofNullable(employee.getSupervisor())
                .map(s -> s.getEmployeeName())
                .orElse(null);

        List<String> roleNames = employee.getEmployeeRoles().stream()
                .map(er -> er.getRole().getName())
                .collect(Collectors.toList());
        EmployeeDTO dto = new EmployeeDTO(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getEmployeeName(),
                roleNames,
                employee.getDepartment().getDepartmentName(),
                employee.getPosition().getPositionName(),
                supervisorCode,
                supervisorName,
                employee.getHireDate(),
                employee.getMonthsOfService()
        );

        assertThat(dto.getEmployeeCode()).isEqualTo("test123");
        assertThat(dto.getEmployeeName()).isEqualTo("Test Employee");
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("EMPLOYEE");
    }
}

