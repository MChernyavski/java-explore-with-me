package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.exceptions.TimeException;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.mapper.ViewStatsMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.StatRepository;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    @Transactional
    public EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        EndpointHit hitNew = statRepository.save(endpointHit);
        return EndpointHitMapper.toEndpointHitDto(hitNew);
    }

    @Override
    public List<ViewStatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start == null || end == null) {
            throw new TimeException("Error with start time and end time");
        }
        if (start.isAfter(end)) {
            throw new TimeException("Error with start time and end time");
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return ViewStatsMapper.listToViewStatsDto(statRepository.findAllUniqueStatsByDateBetween(start, end));
            } else {
                return ViewStatsMapper.listToViewStatsDto(statRepository.findAllStatsByDateBetween(start, end));
            }
        } else {
            if (unique) {
                return ViewStatsMapper.listToViewStatsDto(statRepository.findAllUniqueStatsUriByDateBetween(start, end, uris));
            } else {
                return ViewStatsMapper.listToViewStatsDto(statRepository.findAllStatsUriByDateBetween(start, end, uris));
            }
        }
    }
}
