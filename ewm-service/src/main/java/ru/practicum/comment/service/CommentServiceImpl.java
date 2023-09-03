package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateRequestCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CommentConflictException;
import ru.practicum.exception.EventConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto addCommentByAuthor(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " doesn't exist"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " doesn't exist"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventConflictException("You can't add a comment to an unpublished event");
        }

        Comment comment = CommentMapper.toComment(newCommentDto, user, event);
        Comment newComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(newComment);
    }

    @Override
    @Transactional
    public CommentDto editCommentByAuthor(Long commentId, Long userId, UpdateRequestCommentDto updateCommentDto) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId);
        if (comment == null) {
            throw new CommentConflictException("Comment with id " + commentId + " created by user with id " + userId + " doesn't exist");
        }

        comment.setText(updateCommentDto.getText());
        Comment updComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(updComment);
    }

    @Override
    @Transactional
    public void deleteCommentByAuthor(Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId);
        if (comment == null) {
            throw new NotFoundException("Comment with id " + commentId + " created by user with id " + userId + " doesn't exist");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllCommentsByAuthor(Long userId, Integer from, Integer size) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " doesn't exist"));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Comment> authorComments = commentRepository.findAllByAuthorId(userId, pageRequest);
        return authorComments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    //admin
    @Override
    @Transactional
    public CommentDto editCommentByAdmin(Long commentId, UpdateRequestCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("User with id " + commentId + " doesn't exist"));
        comment.setText(updateCommentDto.getText());
        Comment updComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(updComment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("User with id " + commentId + " doesn't exist"));
        commentRepository.deleteById(commentId);
    }

    // public
    @Override
    public List<CommentDto> getAllCommentsForEventId(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " doesn't exist"));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageRequest);
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("User with id " + commentId + " doesn't exist"));
        return CommentMapper.toCommentDto(comment);
    }
}

