package com.example.springboot_backend.shared.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
