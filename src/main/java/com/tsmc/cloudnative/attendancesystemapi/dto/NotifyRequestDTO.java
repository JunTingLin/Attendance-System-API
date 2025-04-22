package com.tsmc.cloudnative.attendancesystemapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotifyRequestDTO {

    @JsonProperty("application_id")  // 對應 JSON 傳進來的欄位
    private Integer applicationId;
}
