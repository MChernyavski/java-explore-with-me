package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EventConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestConflictException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id {} doesn't exist " + userId));

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream().
                map(RequestMapper::toParticipationRequestDto).
                collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id {} doesn't exist " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id doesn't exist "));

        if (requestRepository.existsRequestByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestConflictException("You can't add the same request");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new RequestConflictException("The initiator can't add a request to participate in his own event");
        }

        if (event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED)) {
            throw new RequestConflictException("You can't participate in an unpublished event");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new EventConflictException("The limit of requests for participation has been reached");
        }

        Request request;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0L) {
            request = Request.builder()
                    .event(event)
                    .created(LocalDateTime.now())
                    .requester(user)
                    .status(RequestStatus.CONFIRMED)
                    .build();

            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            // eventRepository.save(event);
        } else {
            request = Request.builder()
                    .event(event)
                    .created(LocalDateTime.now())
                    .requester(user)
                    .status(RequestStatus.PENDING)
                    .build();
        }
        Request newRequest = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(newRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id {} doesn't exist " + userId));

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request with id {} doesn't exist " + requestId));

        request.setStatus(RequestStatus.CANCELED);
        request.getEvent().setConfirmedRequests(request.getEvent().getConfirmedRequests() - 1);
        Request updatedRequest = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(updatedRequest);
    }
}

