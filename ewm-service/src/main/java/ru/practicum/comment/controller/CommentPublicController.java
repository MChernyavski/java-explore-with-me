package ru.practicum.comment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsForEventId(@RequestParam Long eventId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting all comments for event with id {}", eventId);
        return commentService.getAllCommentsForEventId(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("Getting comment with id {}", commentId);
        return commentService.getCommentById(commentId);
    }
}
