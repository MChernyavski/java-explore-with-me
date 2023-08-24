package ru.practicum.server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
}