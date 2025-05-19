package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;

    @Mock
    private RestTemplate restTemplate;

    private LeaveApplication leaveApplication;
    private Employee employee;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射注入 @Value 的 telegramBotToken
        var field = NotificationService.class.getDeclaredField("telegramBotToken");
        field.setAccessible(true);
        field.set(notificationService, "fake-telegram-token");

        employee = new Employee();
        employee.setEmployeeCode("EMP001");
        employee.setEmployeeName("Alice");
        employee.setNotifyToken("123456789"); // Telegram Chat ID

        leaveApplication = new LeaveApplication();
        leaveApplication.setApplicationId(1001);
        leaveApplication.setStatus("已核准");
        leaveApplication.setApprovalReason("請好好休息");
        leaveApplication.setEmployee(employee);
    }

    @Test
    void notifyLeaveStatus_success() {
        when(leaveApplicationRepository.findById(1001)).thenReturn(Optional.of(leaveApplication));

        String result = notificationService.notifyLeaveStatus(1001);

        assertEquals("通知已發送", result);
        verify(restTemplate).postForObject(anyString(), any(), eq(String.class));
    }

    @Test
    void notifyLeaveStatus_leaveApplicationNotFound_throwsException() {
        when(leaveApplicationRepository.findById(1001)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                notificationService.notifyLeaveStatus(1001));

        assertEquals("請假單不存在", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void notifyLeaveStatus_notifyTokenMissing_warnsAndReturnsMessage() {
        employee.setNotifyToken(""); // 模擬沒有 chat ID
        when(leaveApplicationRepository.findById(1001)).thenReturn(Optional.of(leaveApplication));

        String result = notificationService.notifyLeaveStatus(1001);

        assertEquals("找不到員工的 Telegram Chat ID（notify_token）", result);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void notifyLeaveStatus_sendTelegramFails_throwsException() {
        when(leaveApplicationRepository.findById(1001)).thenReturn(Optional.of(leaveApplication));
        doThrow(new RuntimeException("Telegram 失敗")).when(restTemplate)
                .postForObject(anyString(), any(), eq(String.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                notificationService.notifyLeaveStatus(1001));

        assertTrue(exception.getMessage().contains("通知發送失敗"));
    }
}
