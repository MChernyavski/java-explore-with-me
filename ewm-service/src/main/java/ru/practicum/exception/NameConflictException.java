package ru.practicum.exception;

public class NameConflictException extends RuntimeException {
        public NameConflictException(final String message) {
            super(message);
        }
}
