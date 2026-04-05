package com.project.velo.exception;

public class NotEnoughRights extends RuntimeException {
    public NotEnoughRights(String message) {
        super(message);
    }
}
