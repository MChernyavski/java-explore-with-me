package ru.practicum.exception;

public class EventConflictException extends RuntimeException {
    public EventConflictException(final String message) {
        super(message);
    }
}
