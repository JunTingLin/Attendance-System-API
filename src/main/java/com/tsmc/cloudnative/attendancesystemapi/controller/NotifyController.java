package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.NotifyRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotifyController {

    private final NotificationService notificationService;

    @PostMapping("/leave-status")
    public ApiResponse<String> notifyLeaveStatus(@RequestBody NotifyRequestDTO request) {
        log.info("收到請假狀態通知請求，請假申請ID: {}", request.getApplicationId());

        try {
            String result = notificationService.notifyLeaveStatus(request.getApplicationId());
            return ApiResponse.success("通知處理成功", result);
        } catch (Exception e) {
            log.error("請假狀態通知處理失敗: {}", e.getMessage(), e);
            return ApiResponse.error(500, e.getMessage());
        }
    }
}