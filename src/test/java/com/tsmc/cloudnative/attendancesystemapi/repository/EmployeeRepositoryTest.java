package com.tsmc.cloudnative.attendancesystemapi.repository;

import com.tsmc.cloudnative.attendancesystemapi.entity.Department;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        // 建立一筆 department
        Department dept = new Department();
        dept.setDepartmentCode("D001");
        dept.setDepartmentName("營運組織");
        departmentRepository.save(dept);

        // 建立一筆 position
        Position pos = new Position();
        pos.setPositionName("組織長");
        pos.setPositionLevel(4);
        positionRepository.save(pos);

        // 建立一筆 employee（關聯 department, position）
        Employee e = new Employee();
        e.setEmployeeCode("EMP_TEST");
        e.setEmployeeName("Test User");
        e.setPassword("secret");
        e.setDepartment(dept);
        e.setPosition(pos);
        e.setHireDate(LocalDate.of(2025, 5, 7));
        e.setMonthsOfService(1);
        // supervisor 可以留空
        employeeRepository.save(e);
    }

    @Test
    void findByEmployeeCode_returnsEmployee() {
        // act
        Optional<Employee> found = employeeRepository.findByEmployeeCode("EMP_TEST");

        // assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmployeeName()).isEqualTo("Test User");
        assertThat(found.get().getDepartment().getDepartmentCode()).isEqualTo("D001");
        assertThat(found.get().getPosition().getPositionName()).isEqualTo("組織長");
    }
}
