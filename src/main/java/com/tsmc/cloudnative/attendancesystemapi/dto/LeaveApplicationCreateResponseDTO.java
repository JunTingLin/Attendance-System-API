package com.tsmc.cloudnative.attendancesystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LeaveApplicationCreateResponseDTO {
    private Integer applicationId;
    private String status;
    private LocalDateTime applicationDatetime;

}
