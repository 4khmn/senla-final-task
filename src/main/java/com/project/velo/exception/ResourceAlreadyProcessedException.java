package com.project.velo.exception;

public class ResourceAlreadyProcessedException extends RuntimeException {
    public ResourceAlreadyProcessedException(String message) {
        super(message);
    }
}
