package com.codementor.ideapool.model;

public class ErrorMessage {

    private int status;

    private String message;

    public ErrorMessage() {
    }

    public ErrorMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
