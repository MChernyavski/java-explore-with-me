package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.event.model.enums.StateAdminAction;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequestDto {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private StateAdminAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
