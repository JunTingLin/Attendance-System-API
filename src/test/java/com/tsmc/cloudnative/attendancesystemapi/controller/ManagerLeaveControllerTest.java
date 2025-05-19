package com.tsmc.cloudnative.attendancesystemapi.controller;

import com.tsmc.cloudnative.attendancesystemapi.common.ApiResponse;
import com.tsmc.cloudnative.attendancesystemapi.dto.LeaveApplicationResponseDTO;
import com.tsmc.cloudnative.attendancesystemapi.dto.ReviewLeaveApplicationDTO;
import com.tsmc.cloudnative.attendancesystemapi.service.LeaveApplicationService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class ManagerLeaveControllerTest {

    @InjectMocks
    private ManagerLeaveController controller;

    @Mock
    private LeaveApplicationService leaveApplicationService;

    @Mock
    private Authentication authentication;

    private final int leaveId = 1001;

    private ReviewLeaveApplicationDTO createReviewRequest(String reason) {
        ReviewLeaveApplicationDTO dto = new ReviewLeaveApplicationDTO();
        dto.setApprovalReason(reason);
        return dto;
    }

    private LeaveApplicationResponseDTO createMockResponse(String status) {
        LeaveApplicationResponseDTO dto = new LeaveApplicationResponseDTO();
        dto.setStatus(status);
        dto.setLeaveHours(8);
        return dto;
    }

    @Test
    void approveLeave_shouldReturnSuccessResponse() {
        ReviewLeaveApplicationDTO request = createReviewRequest("表現良好");
        LeaveApplicationResponseDTO responseDTO = createMockResponse("已核准");

        when(leaveApplicationService.approveLeaveApplication(leaveId, request.getApprovalReason()))
                .thenReturn(responseDTO);

        ApiResponse<LeaveApplicationResponseDTO> response = controller.approveLeave(leaveId, request);

        assertEquals("Leave application approved successfully.", response.getMsg());
        assertEquals("已核准", response.getData().getStatus());
        verify(leaveApplicationService, times(1))
                .approveLeaveApplication(leaveId, request.getApprovalReason());
    }

    @Test
    void rejectLeave_shouldReturnSuccessResponse() {
        ReviewLeaveApplicationDTO request = createReviewRequest("表現不佳");
        LeaveApplicationResponseDTO responseDTO = createMockResponse("已駁回");

        when(leaveApplicationService.rejectLeaveApplication(leaveId, request.getApprovalReason()))
                .thenReturn(responseDTO);

        ApiResponse<LeaveApplicationResponseDTO> response = controller.rejectLeave(leaveId, request);

        assertEquals("Leave application rejected successfully.", response.getMsg());
        assertEquals("已駁回", response.getData().getStatus());
        verify(leaveApplicationService, times(1))
                .rejectLeaveApplication(leaveId, request.getApprovalReason());
    }
}
