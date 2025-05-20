package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveBalanceDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.EmployeeLeaveBalance;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveType;
import com.tsmc.cloudnative.attendancesystemapi.repository.EmployeeLeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")




class LeaveBalanceServiceTest {

    @InjectMocks
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;

    @Mock
    private EmployeeService employeeService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setEmployeeId(123);
        mockEmployee.setEmployeeName("測試員工");
    }

    @Test
    void getEmployeeLeaveBalances_shouldDelegateToCurrentYearMethod() {
        // spy is used for partial mock
        LeaveBalanceService spyService = spy(leaveBalanceService);

        doReturn(Collections.emptyList())
                .when(spyService).getEmployeeLeaveBalancesForYear(anyString(), anyInt());

        spyService.getEmployeeLeaveBalances("EMP001");

        verify(spyService, times(1)).getEmployeeLeaveBalancesForYear(eq("EMP001"), eq(LocalDate.now().getYear()));
    }

    @Test
    void getEmployeeLeaveBalancesForYear_shouldReturnMappedDTOs() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(mockEmployee);

        EmployeeLeaveBalance balance = new EmployeeLeaveBalance();
        balance.setBalanceId(1);
        balance.setEmployee(mockEmployee);
        balance.setLeaveYear(2025);
        balance.setTotalHours(80);
        balance.setUsedHours(24);
        balance.setRemainingHours(56);

        LeaveType leaveType = new LeaveType();
        leaveType.setLeaveTypeId(2);
        leaveType.setLeaveTypeName("特休");
        balance.setLeaveType(leaveType);

        when(employeeLeaveBalanceRepository
                .findByEmployeeEmployeeIdAndLeaveYearOrderByLeaveTypeLeaveTypeId(123, 2025))
                .thenReturn(List.of(balance));

        List<LeaveBalanceDTO> result = leaveBalanceService.getEmployeeLeaveBalancesForYear("EMP001", 2025);

        assertEquals(1, result.size());
        LeaveBalanceDTO dto = result.get(0);
        assertEquals(1, dto.getBalanceId());
        assertEquals(123, dto.getEmployeeId());
        assertEquals("測試員工", dto.getEmployeeName());
        assertEquals(2, dto.getLeaveTypeId());
        assertEquals("特休", dto.getLeaveTypeName());
        assertEquals(2025, dto.getLeaveYear());
        assertEquals(80, dto.getTotalHours());
        assertEquals(24, dto.getUsedHours());
        assertEquals(56, dto.getRemainingHours());
    }

    @Test
    void getEmployeeLeaveBalancesForYear_shouldReturnEmptyListWhenNoBalances() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(mockEmployee);

        when(employeeLeaveBalanceRepository
                .findByEmployeeEmployeeIdAndLeaveYearOrderByLeaveTypeLeaveTypeId(123, 2025))
                .thenReturn(Collections.emptyList());

        List<LeaveBalanceDTO> result = leaveBalanceService.getEmployeeLeaveBalancesForYear("EMP001", 2025);

        assertTrue(result.isEmpty());
    }

    @Test
    void getRemainingHoursByTypeId_shouldReturnRemainingHours() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(mockEmployee);

        EmployeeLeaveBalance balance = new EmployeeLeaveBalance();
        balance.setRemainingHours(40);

        when(employeeLeaveBalanceRepository
                .findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(eq(123), eq(2), anyInt()))
                .thenReturn(Optional.of(balance));

        Integer result = leaveBalanceService.getRemainingHoursByTypeId("EMP001", 2);

        assertEquals(40, result);
    }

    @Test
    void getRemainingHoursByTypeId_shouldThrowExceptionWhenNotFound() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(mockEmployee);

        when(employeeLeaveBalanceRepository
                .findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(eq(123), eq(2), anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                leaveBalanceService.getRemainingHoursByTypeId("EMP001", 2)
        );

        assertEquals("查無該假別ID的剩餘時數紀錄", ex.getMessage());
    }
}
