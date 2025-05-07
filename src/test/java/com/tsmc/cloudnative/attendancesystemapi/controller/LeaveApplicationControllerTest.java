package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationRequestDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveApplicationService;
import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

public class LeaveApplicationControllerTest {

    @InjectMocks
    private LeaveApplicationController controller;

    @Mock
    private LeaveApplicationService leaveApplicationService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void applyLeave_shouldReturnSuccessResponse() {
        LeaveApplicationRequestDTO request = new LeaveApplicationRequestDTO();
        request.setLeaveTypeId(1);
        request.setStartDateTime(LocalDateTime.of(2025, 5, 10, 9, 0));
        request.setEndDateTime(LocalDateTime.of(2025, 5, 10, 17, 0));
        request.setLeaveHours(8);
        request.setProxyEmployeeCode("EMP002");
        request.setFilePath("file.pdf");
        request.setFileName("file.pdf");

        LeaveApplicationResponseDTO responseDTO = new LeaveApplicationResponseDTO();
        responseDTO.setLeaveHours(8);
        responseDTO.setStatus("待審核");

        when(authentication.getName()).thenReturn("EMP001");
        when(leaveApplicationService.applyLeave("EMP001", request)).thenReturn(responseDTO);

        ApiResponse<LeaveApplicationResponseDTO> response = controller.applyLeave(authentication, request);

        assertEquals("請假申請成功", response.getMsg());
        assertEquals(8, response.getData().getLeaveHours());
        assertEquals("待審核", response.getData().getStatus());
    }
}
