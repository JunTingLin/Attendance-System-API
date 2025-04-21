package com.tsmc.cloudnative.attendancesystemapi.common;

public enum LeaveStatus {
    PENDING("待審核"),
    APPROVED("已核准"),
    REJECTED("已駁回"),
    CANCELLED("已取消");

    private final String chineseName;

    LeaveStatus(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getChineseName() {
        return chineseName;
    }
}