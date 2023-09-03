package ru.practicum.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShorDto(comment.getAuthor()))
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .createdOn(comment.getCreatedOn())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
    }
}
