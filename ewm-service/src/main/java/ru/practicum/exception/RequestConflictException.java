package ru.practicum.exception;

public class RequestConflictException extends RuntimeException {
    public RequestConflictException(final String message) {
        super(message);
    }
}

