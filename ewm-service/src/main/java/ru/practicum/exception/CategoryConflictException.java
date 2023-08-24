package ru.practicum.exception;

public class CategoryConflictException extends RuntimeException {
    public CategoryConflictException(final String message) {
        super(message);
    }
}
