package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeRole;
import com.tsmc.cloudnative.attendancesystemapi.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRoleRepository employeeRoleRepository;


    @BeforeEach
    void setup() {
        Employee employee = new Employee();
        employee.setEmployeeCode("test123");
        employee.setEmployeeName("Test Employee");
        employee.setPassword("testPass");
        employee.setDepartmentId(1);
        employee.setPositionId(1);
        employee.setHireDate(new Date());
        employee.setYearsOfService(1);
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
        Employee employee = employeeRepository.findByEmployeeCode("test123") // 到這裡只會回傳Optional<Employee> 所以要解包 Optional，若找不到資料就拋出異常
                .orElseThrow(() -> new RuntimeException("找不到該員工"));

        List<String> roleNames = employee.getEmployeeRoles().stream()
                .map(er -> er.getRole().getName())
                .collect(Collectors.toList());

        EmployeeDTO dto = new EmployeeDTO(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getEmployeeName(),
                roleNames
        );

        System.out.println(dto);


    }
}

