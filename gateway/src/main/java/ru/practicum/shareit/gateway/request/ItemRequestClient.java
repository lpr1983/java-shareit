package ru.practicum.shareit.gateway.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Slf4j
@Component
public class ItemRequestClient extends BaseClient {
    private static final String BASE_PATH = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BASE_PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(CreateItemRequestDTO dto, int userId) {
        log.debug("POST /requests userId={} body={}", userId, dto);
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getMyRequests(int userId) {
        log.debug("GET /requests userId={}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getById(int requestId) {
        log.debug("GET /requests/{}", requestId);
        return get("/" + requestId);
    }

    public ResponseEntity<Object> getAllRequestsExceptMy(int userId, int from, int size) {
        log.debug("GET /requests/all userId={} from={} size={}", userId, from, size);
        return get("/all?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }
}