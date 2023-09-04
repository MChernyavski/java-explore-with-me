package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdateRequestCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/comments")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentAdminController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto editCommentByAdmin(@RequestBody @Valid UpdateRequestCommentDto updateCommentDto) {
        log.info("Comment with id {} edited by admin", updateCommentDto.getCommentId());
        return commentService.editCommentByAdmin(updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Comment with id {} deleted by admin", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
