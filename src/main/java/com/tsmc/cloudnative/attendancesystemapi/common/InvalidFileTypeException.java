package com.tsmc.cloudnative.attendancesystemapi.common;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
