package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.*;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee; // 可共用的測試對象

    @BeforeEach
    public void setUp() {
        // 初始化 Employee 和相關屬性
        employee = new Employee();
        employee.setEmployeeId(1);
        employee.setEmployeeCode("emp123");
        employee.setEmployeeName("John Doe");

        Department department = new Department();
        department.setDepartmentName("Test Department");
        department.setDepartmentCode("D001");
        employee.setDepartment(department);

        Position position = new Position();
        position.setPositionName("Test Position");
        employee.setPosition(position);

        Role role = new Role();
        role.setName("MANAGER");
        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setRole(role);
        employee.setEmployeeRoles(Set.of(employeeRole));
    }

    @Test
    public void testGetEmployeeDTOByCode_Success() {
        String employeeCode = "emp123";

        // Mock Repository 返回準備好的 employee
        when(employeeRepository.findByEmployeeCode(employeeCode)).thenReturn(Optional.of(employee));

        // 呼叫目標方法
        EmployeeDTO dto = employeeService.getEmployeeDTOByCode(employeeCode);

        // 驗證結果
        assertThat(dto.getEmployeeId()).isEqualTo(1);
        assertThat(dto.getEmployeeCode()).isEqualTo("emp123");
        assertThat(dto.getEmployeeName()).isEqualTo("John Doe");
        assertThat(dto.getRoleNames()).containsExactlyInAnyOrder("MANAGER");
    }

    @Test
    public void testGetEmployeeDTOByCode_EmployeeNotFound() {
        String employeeCode = "nonexistent";

        // 模擬當找不到員工時的行為
        when(employeeRepository.findByEmployeeCode(employeeCode)).thenReturn(Optional.empty());

        // 驗證例外行為
        assertThatThrownBy(() -> employeeService.getEmployeeDTOByCode(employeeCode))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("員工不存在");
    }
}
