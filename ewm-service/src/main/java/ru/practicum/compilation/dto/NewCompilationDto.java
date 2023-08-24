package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
