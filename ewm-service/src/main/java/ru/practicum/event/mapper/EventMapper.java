package ru.practicum.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShorDto(event.getInitiator()))
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .requestModeration(event.getRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .views(event.getViews())
                .countComments(event.getCountComments())
                .build();
    }

   public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserMapper.toUserShorDto(event.getInitiator()))
                .views(event.getViews())
                .countComments(event.getCountComments())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .initiator(user)
                .category(category)
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .confirmedRequests(0L)
                .views(0L)
                .build();
    }
}


