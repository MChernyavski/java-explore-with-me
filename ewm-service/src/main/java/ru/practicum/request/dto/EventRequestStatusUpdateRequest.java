package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.request.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
