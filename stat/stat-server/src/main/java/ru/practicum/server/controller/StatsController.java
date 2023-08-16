package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatService service;

    @PostMapping("/hit")
    public void addEndpointHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Creating request for uri {}", endpointHitDto.getUri());
        service.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Getting statistics");
        return service.getStat(start, end, uris, unique);
    }
}
