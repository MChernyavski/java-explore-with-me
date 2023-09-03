package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateRequestCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createCommentByAuthor(@PathVariable Long userId, @RequestParam Long eventId,
                                            @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Author with id {} added comment to event with id {}", userId, eventId);
        return commentService.addCommentByAuthor(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editCommentByAuthor(@PathVariable Long commentId, @PathVariable Long userId,
                                          @RequestBody @Valid UpdateRequestCommentDto updateCommentDto) {
        log.info("Comment with id {} edited by author with id {}", commentId, userId);
        return commentService.editCommentByAuthor(commentId, userId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAuthor(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Comment with {} deleted by author with id {} ", commentId, userId);
        commentService.deleteCommentByAuthor(commentId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsByAuthor(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all comments by author with id {} ", userId);
        return commentService.getAllCommentsByAuthor(userId, from, size);
    }
}
