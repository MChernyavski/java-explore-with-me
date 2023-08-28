package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequestDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsByUser(@PathVariable Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all events by initiator {} ", userId);
        return eventService.getAllEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
        public EventFullDto addEvent(@PathVariable @Valid Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Creating new event: {}.", newEventDto.getTitle());
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Getting information about event: {}.", eventId);
        return eventService.getEventByIdAndUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK) //1
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId,
                                    @RequestBody @Valid UpdateEventUserRequestDto updateEventRequestDto) {
        log.info("Updated event: {}.", eventId);
        return eventService.updateEvent(userId, eventId, updateEventRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getAllEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Getting all requests for event: {}.", eventId);
        return eventService.getAllEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatusForEvent(@PathVariable @Positive Long eventId,
                                                                      @PathVariable @Positive Long userId,
                                                                      @RequestBody @Valid
                                                                         EventRequestStatusUpdateRequest
                                                                                 eventRequestStatusUpdate) {
        log.info("Updated request status for event: {}.", eventId);
        return eventService.updateRequestStatusForEvent(eventRequestStatusUpdate, eventId, userId);
    }
}
