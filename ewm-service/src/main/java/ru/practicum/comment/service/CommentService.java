package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateRequestCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addCommentByAuthor(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto editCommentByAuthor(Long commentId, Long userId, UpdateRequestCommentDto updateCommentDto);

    void deleteCommentByAuthor(Long commentId, Long userId);

    List<CommentDto> getAllCommentsByAuthor(Long userId, Integer from, Integer size);

    CommentDto editCommentByAdmin(Long commentId, UpdateRequestCommentDto updateCommentDto);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> getAllCommentsForEventId(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);
}
