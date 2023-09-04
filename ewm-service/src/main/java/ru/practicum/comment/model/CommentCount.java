package ru.practicum.comment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CommentCount {
    private Long eventId;
    private Long commentCount;

    public CommentCount(Long eventId, Long commentCount) {
        this.eventId = eventId;
        this.commentCount = commentCount;
    }
}