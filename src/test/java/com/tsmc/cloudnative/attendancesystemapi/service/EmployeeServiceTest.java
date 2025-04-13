package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.EmployeeDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.*;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeRepository;
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

    @Test
    public void testGetEmployeeDTOByCode_Success() {
        String employeeCode = "emp123";
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setEmployeeCode("emp123");
        employee.setEmployeeName("John Doe");

        // 新增 Department 和 Position，以避免 NullPointerException
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

        // 當呼叫 repository 查詢指定 employeeCode 時，返回模擬的 employee
        when(employeeRepository.findByEmployeeCode(employeeCode)).thenReturn(Optional.of(employee));

        EmployeeDTO dto = employeeService.getEmployeeDTOByCode(employeeCode);


        // Assert: 驗證轉換後的 DTO 是否符合預期
        assertThat(dto.getEmployeeId()).isEqualTo(1);
        assertThat(dto.getEmployeeCode()).isEqualTo("emp123");
        assertThat(dto.getEmployeeName()).isEqualTo("John Doe");
        assertThat(dto.getRoleNames()).containsExactlyInAnyOrder("MANAGER");
    }

    @Test
    public void testGetEmployeeDTOByCode_EmployeeNotFound() {

        String employeeCode = "nonexistent";
        when(employeeRepository.findByEmployeeCode(employeeCode)).thenReturn(Optional.empty());

        // Act & Assert: 驗證當找不到員工時，是否拋出正確例外
        assertThatThrownBy(() -> employeeService.getEmployeeDTOByCode(employeeCode))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("員工不存在");
    }
}