package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {

    @Value("${ewm-service.app}")
    private String app;

    @Autowired
    public StatClient(@Value("${stats.service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

   public void addStat(HttpServletRequest request) {
       EndpointHitDto endpointHitDto = new EndpointHitDto();
       endpointHitDto.setApp(app);
       endpointHitDto.setUri(request.getRequestURI());
       endpointHitDto.setIp(request.getRemoteAddr());
       endpointHitDto.setTimestamp(LocalDateTime.now());

       ResponseEntity<Object> response = post("/hit", endpointHitDto);
   }

    public ResponseEntity<Object> getStat(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris",  String.join(",", uris), // uris,
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}