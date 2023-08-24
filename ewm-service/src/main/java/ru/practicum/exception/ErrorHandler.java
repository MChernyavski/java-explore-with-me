package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleNameConflictException(final NameConflictException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCategoryConflictException(final CategoryConflictException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleWrongTimeException(final WrongTimeException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventConflictException(final EventConflictException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidateException(final ValidateException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRequestConflictException(final RequestConflictException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.toString())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

