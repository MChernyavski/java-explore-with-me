package ru.practicum.client;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

@Service
public class StatClient extends BaseClient {

    private static final String SERVER_URL = "http://localhost:9090";
    private static final String API_PREFIX = "http://stats-server:9090";

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addStat(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
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