package com.project.velo.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Exception ex) {
        super(message, ex);
    }
}
