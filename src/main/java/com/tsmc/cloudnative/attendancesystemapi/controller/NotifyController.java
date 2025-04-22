package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.dto.NotifyRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotifyController {

    private final LeaveApplicationRepository leaveApplicationRepository;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @PostMapping("/leave-status")
    public ResponseEntity<String> notifyLeaveStatus(@RequestBody NotifyRequestDTO request) {
        Integer applicationId = request.getApplicationId();

        LeaveApplication application = leaveApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("請假單不存在"));

        String status = application.getStatus();
        String comment = application.getApprovalReason();
        Employee employee = application.getEmployee();

        String telegramChatId = employee.getNotifyToken();
        if (telegramChatId == null || telegramChatId.isEmpty()) {
            return ResponseEntity.badRequest().body("找不到員工的 Telegram Chat ID（notify_token）");
        }

        String message = String.format(
                "您好 %s，您的假單 (ID: %d) 已被審核。\n結果：%s\n主管備註：%s",
                employee.getEmployeeName(),
                applicationId,
                status,
                comment != null ? comment : "無"
        );

        sendTelegramMessage(telegramChatId, message);

        return ResponseEntity.ok("通知已發送");
    }

    private void sendTelegramMessage(String chatId, String text) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", telegramBotToken);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", text);

        restTemplate.postForObject(url, params, String.class);
    }
}
