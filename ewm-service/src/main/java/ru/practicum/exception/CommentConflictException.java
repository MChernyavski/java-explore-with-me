package ru.practicum.exception;

public class CommentConflictException extends RuntimeException {
    public CommentConflictException(final String message) {
        super(message);
    }
}
