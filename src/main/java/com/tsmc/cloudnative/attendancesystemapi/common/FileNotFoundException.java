package com.tsmc.cloudnative.attendancesystemapi.common;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}
