package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationListDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.*;
import com.tsmc.cloudnative.attendancesystemapi.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
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

    @Mock
    private NotificationService notificationService;

    private LeaveApplicationRequestDTO validRequest;
    private Employee employee;
    private LeaveType leaveType;
    private EmployeeLeaveBalance balance;
    private Employee proxy;
    private LeaveApplication validLeaveApplication;

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

        validLeaveApplication = new LeaveApplication();
        validLeaveApplication.setApplicationId(123);
        validLeaveApplication.setEmployee(employee);
        validLeaveApplication.setLeaveType(leaveType);
        validLeaveApplication.setStartDatetime(LocalDateTime.of(2025, 5, 10, 9, 0));
        validLeaveApplication.setEndDatetime(LocalDateTime.of(2025, 5, 10, 17, 0));
        validLeaveApplication.setLeaveHours(8);
        validLeaveApplication.setReason("Personal");
        validLeaveApplication.setProxyEmployee(proxy);
    }

    @Test
    void getEmployeeLeaveApplications_shouldReturnList() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveApplicationRepository.findByEmployeeEmployeeIdOrderByApplicationDatetimeDesc(employee.getEmployeeId()))
                .thenReturn(Arrays.asList(validLeaveApplication));

        List<LeaveApplicationListDTO> result = leaveApplicationService.getEmployeeLeaveApplications("EMP001");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getEmployeeLeaveApplicationDetail_shouldReturnDetail() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeEmployeeId(
                validLeaveApplication.getApplicationId(), employee.getEmployeeId()))
                .thenReturn(Optional.of(validLeaveApplication));

        LeaveApplicationResponseDTO response = leaveApplicationService.getEmployeeLeaveApplicationDetail("EMP001", validLeaveApplication.getApplicationId());

        assertNotNull(response);
        assertEquals(validLeaveApplication.getLeaveHours(), response.getLeaveHours());
    }

    @Test
    void getEmployeeLeaveApplicationDetail_notFound_shouldThrow() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeEmployeeId(
                validLeaveApplication.getApplicationId(), employee.getEmployeeId()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> {
            leaveApplicationService.getEmployeeLeaveApplicationDetail("EMP001", validLeaveApplication.getApplicationId());
        });

        assertEquals("請假記錄不存在或無權限查看", ex.getMessage());
    }

    @Test
    void getManagerSubordinatesLeaveApplications_shouldReturnList() {
        when(employeeService.findEmployeeByCode("MGR001")).thenReturn(employee);
        when(leaveApplicationRepository.findByEmployeeSupervisorEmployeeIdOrderByApplicationDatetimeDesc(employee.getEmployeeId()))
                .thenReturn(Arrays.asList(validLeaveApplication));

        List<LeaveApplicationListDTO> result = leaveApplicationService.getManagerSubordinatesLeaveApplications("MGR001");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getManagerLeaveApplicationDetail_shouldReturnDetail() {
        when(employeeService.findEmployeeByCode("MGR001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeSupervisorEmployeeId(
                validLeaveApplication.getApplicationId(), employee.getEmployeeId()))
                .thenReturn(Optional.of(validLeaveApplication));

        LeaveApplicationResponseDTO response = leaveApplicationService.getManagerLeaveApplicationDetail("MGR001", validLeaveApplication.getApplicationId());

        assertNotNull(response);
        assertEquals(validLeaveApplication.getLeaveHours(), response.getLeaveHours());
    }

    @Test
    void getManagerLeaveApplicationDetail_notFound_shouldThrow() {
        when(employeeService.findEmployeeByCode("MGR001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeSupervisorEmployeeId(
                validLeaveApplication.getApplicationId(), employee.getEmployeeId()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> {
            leaveApplicationService.getManagerLeaveApplicationDetail("MGR001", validLeaveApplication.getApplicationId());
        });

        assertEquals("請假記錄不存在或您無權限查看", ex.getMessage());
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

    @Test
    void updateLeaveApplication_success() {
        // Arrange
        int applicationId = 123;
        validRequest.setStatus("待審核");

        LeaveApplication existingApplication = new LeaveApplication();
        existingApplication.setApplicationId(applicationId);
        existingApplication.setEmployee(employee);
        existingApplication.setStatus("待審核");

        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeEmployeeId(applicationId, 100))
                .thenReturn(Optional.of(existingApplication));
        when(leaveTypeRepository.findById(1)).thenReturn(Optional.of(leaveType));
        when(employeeLeaveBalanceRepository.findByEmployeeEmployeeIdAndLeaveTypeLeaveTypeIdAndLeaveYear(
                eq(100), eq(1), anyInt())).thenReturn(Optional.of(balance));
        when(employeeService.findEmployeeByCode("EMP002")).thenReturn(proxy);
        when(leaveApplicationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LeaveApplicationResponseDTO response = leaveApplicationService.updateEmployeeLeaveApplication(
                "EMP001", applicationId, validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(8, response.getLeaveHours());
    }

    @Test
    void updateLeaveApplication_notFoundOrUnauthorized() {
        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeEmployeeId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> leaveApplicationService.updateEmployeeLeaveApplication("EMP001", 999, validRequest));
        assertEquals("請假記錄不存在或無權限修改", ex.getMessage());
    }

    @Test
    void updateLeaveApplication_alreadyReviewed() {
        LeaveApplication reviewed = new LeaveApplication();
        reviewed.setStatus("已核准");
        reviewed.setEmployee(employee);

        when(employeeService.findEmployeeByCode("EMP001")).thenReturn(employee);
        when(leaveApplicationRepository.findByApplicationIdAndEmployeeEmployeeId(anyInt(), anyInt()))
                .thenReturn(Optional.of(reviewed));

        Exception ex = assertThrows(IllegalStateException.class,
                () -> leaveApplicationService.updateEmployeeLeaveApplication("EMP001", 123, validRequest));
        assertEquals("該筆請假記錄已被審核，無法修改", ex.getMessage());
    }


    private void setupReviewScenario(String status, boolean shouldNotificationSucceed) {
        Integer leaveId = validLeaveApplication.getApplicationId();
        String reason = validLeaveApplication.getReason();

        when(leaveApplicationRepository.findById(leaveId)).thenReturn(Optional.of(validLeaveApplication));

        validLeaveApplication.setStatus(status);
        when(leaveApplicationRepository.save(any())).thenReturn(validLeaveApplication);

        if (shouldNotificationSucceed) {
            when(notificationService.notifyLeaveStatus(leaveId)).thenReturn("通知已發送");
        } else {
            when(notificationService.notifyLeaveStatus(leaveId)).thenThrow(new RuntimeException("通知發送失敗"));
        }
    }

    private void assertReviewResultAndCapture(LeaveApplicationResponseDTO result, String expectedStatus) {
        assertEquals(expectedStatus, result.getStatus());

        ArgumentCaptor<LeaveApplication> captor = ArgumentCaptor.forClass(LeaveApplication.class);
        verify(leaveApplicationRepository).save(captor.capture());

        LeaveApplication captured = captor.getValue();
        assertEquals(expectedStatus, captured.getStatus());
        assertEquals(validLeaveApplication.getReason(), captured.getApprovalReason());

        verify(notificationService).notifyLeaveStatus(validLeaveApplication.getApplicationId());
    }


    @Test
    void approveLeaveApplication_shouldUpdateStatusAndSendNotification() {
        setupReviewScenario("已核准", true);

        LeaveApplicationResponseDTO result = leaveApplicationService.approveLeaveApplication(
                validLeaveApplication.getApplicationId(),
                validLeaveApplication.getReason()
        );

        assertReviewResultAndCapture(result, "已核准");
    }

    @Test
    void approveLeaveApplication_shouldUpdateStatusAndFailToSendNotification() {
        setupReviewScenario("已核准", false);

        LeaveApplicationResponseDTO result = leaveApplicationService.approveLeaveApplication(
                validLeaveApplication.getApplicationId(),
                validLeaveApplication.getReason()
        );

        assertReviewResultAndCapture(result, "已核准");
    }

    @Test
    void rejectLeaveApplication_shouldUpdateStatusAndSendNotification() {
        setupReviewScenario("已駁回", true);

        LeaveApplicationResponseDTO result = leaveApplicationService.rejectLeaveApplication(
                validLeaveApplication.getApplicationId(),
                validLeaveApplication.getReason()
        );

        assertReviewResultAndCapture(result, "已駁回");
    }

    @Test
    void rejectLeaveApplication_shouldUpdateStatusAndFailToSendNotification() {
        setupReviewScenario("已駁回", false);

        LeaveApplicationResponseDTO result = leaveApplicationService.rejectLeaveApplication(
                validLeaveApplication.getApplicationId(),
                validLeaveApplication.getReason()
        );

        assertReviewResultAndCapture(result,"已駁回");
    }
}
