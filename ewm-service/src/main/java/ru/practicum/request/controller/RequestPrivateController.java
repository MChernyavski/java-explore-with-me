package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable long userId) {
        log.info("Getting all requests by user with id {}", userId);
        return requestService.getAllUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @RequestParam @NotNull Long eventId) {
        log.info("Added new request by user {} for event {}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Canceled request {} by user {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
