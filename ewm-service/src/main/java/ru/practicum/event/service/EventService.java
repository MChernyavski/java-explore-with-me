package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequestDto updateEventRequestDto);

    EventRequestStatusUpdateResult updateRequestStatusForEvent(Long eventId, Long userId,
                                                               EventRequestStatusUpdateRequest
                                                                          eventRequestStatusUpdateRequest);

    List<EventShortDto> getAllEventsByUser(long userId, int from, int size);

    EventFullDto getEventByIdAndUser(long userId, long eventId);

    List<ParticipationRequestDto> getAllEventRequests(long userId, long eventId);

    List<EventFullDto> searchEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllEventsPublic(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                           Integer from, Integer size, HttpServletRequest request);

}
