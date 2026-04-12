package com.project.velo.exception;

public class FileNotFoundCustomException extends RuntimeException {
    public FileNotFoundCustomException(String message) {
        super(message);
    }
}
