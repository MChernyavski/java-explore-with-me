package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private float lat; // широта
    private float lon; // долгота
}
