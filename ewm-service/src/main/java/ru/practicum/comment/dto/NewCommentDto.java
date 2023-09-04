package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {
    @NotNull
    private Long eventId;
    @NotBlank
    @Size(min = 1, max = 1000)
    private String text;
}
