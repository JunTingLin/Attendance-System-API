package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.*;
import com.tsmc.cloudnative.attendancesystemapi.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveApplicationServiceTest {

    @InjectMocks
    private LeaveApplicationService leaveApplicationService;

    @Mock
    private EmployeeService employeeService;
    @Mock
    private LeaveTypeRepository leaveTypeRepository;
    @Mock
    private EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;
    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    private LeaveApplicationRequestDTO validRequest;
    private Employee employee;
    private LeaveType leaveType;
    private EmployeeLeaveBalance balance;
    private Employee proxy;

    @BeforeEach
    void setUp() {
        validRequest = new LeaveApplicationRequestDTO();
        validRequest.setLeaveTypeId(1);
        validRequest.setStartDateTime(LocalDateTime.of(2025, 5, 10, 9, 0));
        validRequest.setEndDateTime(LocalDateTime.of(2025, 5, 10, 17, 0));
        validRequest.setLeaveHours(8);
        validRequest.setReason("Personal");
        validRequest.setProxyEmployeeCode("EMP002");
        validRequest.setFilePath("file.pdf");
        validRequest.setFileName("file.pdf");

        employee = new Employee();
        employee.setEmployeeId(100);

        leaveType = new LeaveType();
        leaveType.setLeaveTypeId(1);
        leaveType.setAttachmentRequired(false);

        balance = new EmployeeLeaveBalance();
        balance.setRemainingHours(10);

        proxy = new Employee();
        proxy.setEmployeeId(200);
    }

    @Test
    void applyLeave_success() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(employeeLeaveBalanceRepository.findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                eq(100), eq(1), anyInt())).thenReturn(Optional.of(balance));
        when(employeeService.findEmployeeByCode("EMP002")).thenReturn(proxy);

        LeaveApplication saved = new LeaveApplication();
        saved.setLeaveHours(8);
        saved.setStatus("待審核");
        saved.setEmployee(employee);
        saved.setLeaveType(leaveType);
        saved.setProxyEmployee(proxy);

        when(leaveApplicationRepository.save(any())).thenReturn(saved);

        // Mock convertToResponseDTO (simplified for testing)
        LeaveApplicationResponseDTO response = leaveApplicationService.applyLeave("EMP001", validRequest);

        assertNotNull(response);
        assertEquals(8, response.getLeaveHours());
        assertEquals("待審核", response.getStatus());
    }

    @Test
    void applyLeave_invalidLeaveType() {
        when(employeeService.findEmployeeByCode(any())).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> leaveApplicationService.applyLeave("EMP001", validRequest));
        assertEquals("無效的假別ID", ex.getMessage());
    }

    @Test
    void applyLeave_attachmentRequiredButMissing() {
        leaveType.setAttachmentRequired(true);
        validRequest.setFilePath(null);

        when(employeeService.findEmployeeByCode(any())).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> leaveApplicationService.applyLeave("EMP001", validRequest));
        assertEquals("此假別需上傳附件", ex.getMessage());
    }

    @Test
    void applyLeave_startDateAfterEndDate() {
        validRequest.setStartDateTime(LocalDateTime.of(2025, 5, 10, 18, 0));
        validRequest.setEndDateTime(LocalDateTime.of(2025, 5, 10, 9, 0));

        when(employeeService.findEmployeeByCode(any())).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(employeeLeaveBalanceRepository.findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                eq(100), eq(1), anyInt())).thenReturn(Optional.of(balance));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> leaveApplicationService.applyLeave("EMP001", validRequest));
        assertEquals("開始時間需早於結束時間", ex.getMessage());
    }

    @Test
    void applyLeave_hoursExceedBalance() {
        balance.setRemainingHours(5);
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(employeeLeaveBalanceRepository.findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                eq(100), eq(1), anyInt())).thenReturn(Optional.of(balance));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> leaveApplicationService.applyLeave("EMP001", validRequest));
        assertEquals("請假時數超過可用餘額", ex.getMessage());
    }

    @Test
    void applyLeave_missingBalanceRecord() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(employeeLeaveBalanceRepository.findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                eq(100), eq(1), anyInt())).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> leaveApplicationService.applyLeave("EMP001", validRequest));
        assertEquals("查無該假別的請假餘額", ex.getMessage());
    }

    @Test
    void applyLeave_missingProxyEmployee() {
        validRequest.setProxyEmployeeCode("");

        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(employeeLeaveBalanceRepository.findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                eq(100), eq(1), anyInt())).thenReturn(Optional.of(balance));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> leaveApplicationService.applyLeave("EMP001", validRequest));
        assertEquals("請填寫代理人", ex.getMessage());
    }
}
