package com.tsmc.cloudnative.attendancesystemapi.common;

public class FileAccessDeniedException extends RuntimeException {
    public FileAccessDeniedException(String message) {
        super(message);
    }
}
