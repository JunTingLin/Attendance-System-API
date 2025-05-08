package com.tsmc.cloudnative.attendancesystemapi.service;

import com.tsmc.cloudnative.attendancesystemapi.entity.Employee;
import com.tsmc.cloudnative.attendancesystemapi.entity.LeaveApplication;
import com.tsmc.cloudnative.attendancesystemapi.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;


    public String notifyLeaveStatus(Integer applicationId) {
        log.debug("開始發送請假狀態通知，請假申請ID: {}", applicationId);

        LeaveApplication application = leaveApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("請假單不存在"));

        String status = application.getStatus();
        String comment = application.getApprovalReason();
        Employee employee = application.getEmployee();

        String telegramChatId = employee.getNotifyToken();
        if (telegramChatId == null || telegramChatId.isEmpty()) {
            log.warn("員工 {} 未設定 Telegram Chat ID", employee.getEmployeeCode());
            return "找不到員工的 Telegram Chat ID（notify_token）";
        }

        String message = String.format(
                "您好 %s，您的假單 (ID: %d) 已被審核。\n結果：%s\n主管備註：%s",
                employee.getEmployeeName(),
                applicationId,
                status,
                comment != null ? comment : "無"
        );

        sendTelegramMessage(telegramChatId, message);
        log.info("成功發送請假狀態通知給員工 {}", employee.getEmployeeCode());

        return "通知已發送";
    }


    private void sendTelegramMessage(String chatId, String text) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", telegramBotToken);

        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", text);

        try {
            restTemplate.postForObject(url, params, String.class);
            log.debug("Telegram 訊息已發送至 chat_id: {}", chatId);
        } catch (Exception e) {
            log.error("發送 Telegram 訊息失敗：{}", e.getMessage(), e);
            throw new RuntimeException("通知發送失敗：" + e.getMessage());
        }
    }
}