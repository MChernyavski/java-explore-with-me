package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.enums.EventSort;
import ru.practicum.event.model.enums.StateAdminAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.enums.StateUserAction;
import ru.practicum.exception.*;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.toEventFullDto;
import static ru.practicum.event.model.enums.EventState.PUBLISHED;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id {} doesn't exist " + userId));
        Long catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Сategory with id doesn't exist " + catId));
        Location location = LocationMapper.toLocation(newEventDto.getLocation());
        locationRepository.save(location);

        Event event = EventMapper.toEvent(newEventDto, category, user, location);
        LocalDateTime eventDate = event.getEventDate();

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongTimeException("Field: eventDate. Error: must contain a date that hasn't yet arrived.");
        }

        Event newEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(newEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventRequestDto) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Event with id " + eventId + " doesn't exist");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new EventConflictException("Event already published, only pending or canceled events can be changed");
        }

        if (updateEventRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventRequestDto.getAnnotation());
        }
        if (updateEventRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventRequestDto.getCategory()).orElseThrow(() ->
                    new NotFoundException("Сategory doesn't exist " + updateEventRequestDto.getCategory()));
            event.setCategory(category);
        }

        if (updateEventRequestDto.getDescription() != null) {
            event.setDescription(updateEventRequestDto.getDescription());
        }

        if (updateEventRequestDto.getEventDate() != null) {
            if (updateEventRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidateException("Field: eventDate. Error: must contain a date that hasn't yet arrived.");
            }
            event.setEventDate(updateEventRequestDto.getEventDate());
        }

        if (updateEventRequestDto.getLocation() != null) {
            Location location = event.getLocation();
            location.setLon(updateEventRequestDto.getLocation().getLon());
            location.setLat(updateEventRequestDto.getLocation().getLat());
            locationRepository.save(location);
            event.setLocation(location);
        }

        if (updateEventRequestDto.getPaid() != null) {
            event.setPaid(updateEventRequestDto.getPaid());
        }

        if (updateEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequestDto.getParticipantLimit());
        }

        if (updateEventRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequestDto.getRequestModeration());
        }

        if (updateEventRequestDto.getStateAction() != null) {
            StateUserAction state = updateEventRequestDto.getStateAction();
            switch (state) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new NotFoundException("Invalid format state");
            }
        }

        if (updateEventRequestDto.getTitle() != null) {
            updateEventRequestDto.setTitle(updateEventRequestDto.getTitle());
        }
        Event updateEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatusForEvent(EventRequestStatusUpdateRequest
                                                                              eventRequestStatusUpdate,
                                                                      Long eventId, Long userId) {


        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id doesn't exist"));

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new RequestConflictException("The limit of requests for participation has been reached");
        }

        if (!userId.equals(event.getInitiator().getId())) {
            throw new RequestConflictException("Only the event initiator can change the status of an event request");
        }

        List<Request> requests = requestRepository.findRequestByIdInAndEventId(eventRequestStatusUpdate.getRequestIds(),
                eventId);

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new RequestConflictException("The status can only be changed for requests that are in the pending state");
            } else {
                if (eventRequestStatusUpdate.getStatus().equals(RequestStatus.CONFIRMED)) {
                    if (event.getParticipantLimit() == 0 || event.getParticipantLimit() > event.getConfirmedRequests()) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        request.getEvent().setConfirmedRequests(request.getEvent().getConfirmedRequests() + 1);
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                    }
                } else if (eventRequestStatusUpdate.getStatus().equals(RequestStatus.REJECTED)) {
                    request.setStatus(RequestStatus.REJECTED);
                }
            }
        }
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        List<Request> confirmedRequests = requests.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.CONFIRMED))
                .collect(Collectors.toList());
        List<Request> rejectedRequests = requests.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.REJECTED))
                .collect(Collectors.toList());
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream()
                        .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream()
                        .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<EventShortDto> getAllEventsByUser(Long userId, int from, int size) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id {} doesn't exist " + userId));

        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> hits = getViewsFromStatistic(events);
        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDto)
                .peek(e -> e.setViews(hits.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
        return eventShortDtos;
    }

    @Override
    public EventFullDto getEventByIdAndUser(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Event with id " + eventId + " doesn't exist");
        }
        Map<Long, Long> hits = getViewsFromStatistic(List.of(event));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(hits.getOrDefault(eventFullDto.getId(), 0L));
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getAllEventRequests(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Event with id " + eventId + " doesn't exist");
        }
        return requestRepository.findAllByEventId(eventId).stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  int from, int size) {
        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = rangeStart;
        }
        if (rangeEnd != null) {
            end = rangeEnd;
        }
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new ValidateException("The end date of the search must not be earlier than the start date of the search");
            }
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.getEventsByParam(users, eventStates, categories, start, end, page);
        events.forEach(event -> event.setConfirmedRequests(requestRepository.getConfirmedRequestsByEvent(event.getId())));
        Map<Long, Long> hits = getViewsFromStatistic(events);
        events.forEach(event -> event.setViews(hits.get(event.getId())));
        eventRepository.saveAll(events);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id doesn't exist "));

        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (updateEventAdminRequestDto.getStateAction() == StateAdminAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new EventConflictException("Event can only be published if it's in the publish pending state");
                }
                if (event.getPublishedOn() != null && event.getEventDate().isAfter(event.getPublishedOn().minusHours(1))) {
                    throw new EventConflictException("Event can't be published because it's after 1 hour before event datetime");
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(PUBLISHED);
            }
            if (updateEventAdminRequestDto.getStateAction() == StateAdminAction.REJECT_EVENT) {
                if (event.getState() == PUBLISHED) {
                    throw new EventConflictException("Event can't only be rejected because it's already published");
                } else {
                    event.setState(EventState.CANCELED);
                }
            }
        }

        if (updateEventAdminRequestDto.getEventDate() != null) {
            if (updateEventAdminRequestDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ValidateException("The start time of the event can't be earlier than the current time");
            }
            if (updateEventAdminRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new WrongTimeException("Field: eventDate. Error: must contain a date that hasn't yet arrived.");
            }
            event.setEventDate(updateEventAdminRequestDto.getEventDate());
        }

        if (updateEventAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequestDto.getAnnotation());
        }

        if (updateEventAdminRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequestDto.getCategory()).orElseThrow(() ->
                    new NotFoundException("Сategory doesn't exist " + updateEventAdminRequestDto.getCategory()));
            event.setCategory(category);
        }

        if (updateEventAdminRequestDto.getDescription() != null) {
            event.setDescription(updateEventAdminRequestDto.getDescription());
        }


        if (updateEventAdminRequestDto.getLocation() != null) {
            Location location = event.getLocation();
            location.setLon(updateEventAdminRequestDto.getLocation().getLon());
            location.setLat(updateEventAdminRequestDto.getLocation().getLat());
            event.setLocation(location);
            locationRepository.save(location);
        }

        if (updateEventAdminRequestDto.getPaid() != null) {
            event.setPaid(updateEventAdminRequestDto.getPaid());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        if (updateEventAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequestDto.getRequestModeration());
        }
        if (updateEventAdminRequestDto.getTitle() != null) {
            event.setTitle(updateEventAdminRequestDto.getTitle());
        }
        Event updatedEvent = eventRepository.save(event);
        return toEventFullDto(updatedEvent);
    }

    // public

    @Override
    @Transactional
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event doesn't exist "));
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Can't get information about this event :event must be published");
        }

        eventRepository.save(event);
        statClient.addStat(request);
        Map<Long, Long> hits = getViewsFromStatistic(List.of(event));
        event.setViews(hits.get(event.getId()));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public List<EventShortDto> getAllEventsPublic(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort,
                                                  Integer from,
                                                  Integer size,
                                                  HttpServletRequest request) {

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = new ArrayList<>();

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidateException("Invalid request. The end date of the event is set later than the start date");
        }

        if (onlyAvailable) {
            if (sort == null) {
                events = eventRepository.getAvailableEventsWithFilters(text, PUBLISHED, categories, paid, rangeStart, rangeEnd, pageRequest);
            } else {
                switch (EventSort.valueOf(sort)) {
                    case EVENT_DATE:
                        events = eventRepository.getAvailableEventsWithFilters(text, PUBLISHED, categories, paid, rangeStart, rangeEnd, pageRequest);
                        statClient.addStat(request);
                        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());

                    case VIEWS:
                        events = eventRepository.getAvailableEventsFiltersNoSort(text, PUBLISHED, categories, paid, rangeStart, rangeEnd, pageRequest);
                        Map<Long, Long> hits = getViewsFromStatistic(events);
                        events.forEach(event -> event.setViews(hits.get(event.getId())));
                        statClient.addStat(request);
                        return events.stream()
                                .map(EventMapper::toEventShortDto)
                                .sorted(Comparator.comparing(EventShortDto::getViews))
                                .collect(Collectors.toList());
                }
            }
        } else {
            if (sort == null) {
                events = eventRepository.getAllEventsWithFilters(text, PUBLISHED, categories, paid, rangeStart, rangeEnd, pageRequest);
            } else {
                switch (EventSort.valueOf(sort)) {
                    case EVENT_DATE:
                        events = eventRepository.getAllEventsWithFilters(text,
                                PUBLISHED, categories, paid, rangeStart, rangeEnd, pageRequest);
                        statClient.addStat(request);
                        return events.stream()
                                .map(EventMapper::toEventShortDto)
                                .collect(Collectors.toList());

                    case VIEWS:
                        events = eventRepository.getAllEventsWithFiltersNoSorted(text, PUBLISHED, categories, paid, rangeStart, rangeEnd, pageRequest);
                        Map<Long, Long> hits = getViewsFromStatistic(events);
                        events.forEach(event -> event.setViews(hits.get(event.getId())));
                        statClient.addStat(request);
                        return events.stream()
                                .map(EventMapper::toEventShortDto)
                                .sorted(Comparator.comparing(EventShortDto::getViews))
                                .collect(Collectors.toList());
                }
            }
        }
        statClient.addStat(request);
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    private Map<Long, Long> getViewsFromStatistic(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());

        List<LocalDateTime> eventsDate = new ArrayList<>();

        for (Event event : events) {
            if (event.getPublishedOn() != null) {
                eventsDate.add(event.getPublishedOn());
            } else {
                eventsDate.add(LocalDateTime.now());
            }
        }

        if (eventsDate.isEmpty()) {
            throw new NotFoundException("No published date");
        }

        LocalDateTime firstDate = LocalDateTime.now();
        for (LocalDateTime dateTime : eventsDate) {
            if (dateTime.isBefore(firstDate)) {
                firstDate = dateTime;
            }
        }

        String start = firstDate.format(DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String eventsUri = "/events/";
        List<String> uris = eventIds.stream().map(id -> eventsUri + id).collect(Collectors.toList());
        ResponseEntity<Object> response = statClient.getStat(start, end, uris, true);
        List<ViewStatsDto> viewStatsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        Map<Long, Long> hits = new HashMap<>();

        for (ViewStatsDto statsDto : viewStatsDto) {
            String uri = statsDto.getUri();
            hits.put(Long.parseLong(uri.substring(eventsUri.length())), statsDto.getHits());
        }
        return hits;
    }
}


