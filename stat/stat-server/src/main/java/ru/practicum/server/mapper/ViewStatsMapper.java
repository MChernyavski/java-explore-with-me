package ru.practicum.server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViewStatsMapper {

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

    public static List<ViewStatsDto> listToViewStatsDto(List<ViewStats> list) {
        if (!list.isEmpty()) {
            return list.stream()
                    .map(ViewStatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
