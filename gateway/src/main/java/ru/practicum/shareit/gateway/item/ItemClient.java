package ru.practicum.shareit.gateway.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Slf4j
@Component
public class ItemClient extends BaseClient {
    private static final String BASE_PATH = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BASE_PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(CreateItemDTO itemToCreate, int ownerId) {
        log.debug("POST /items userId={} body={}", ownerId, itemToCreate);
        return post("", ownerId, itemToCreate);
    }

    public ResponseEntity<Object> update(UpdateItemDTO itemToUpdate, int itemId, int ownerId) {
        log.debug("PATCH /items/{} userId={} body={}", itemId, ownerId, itemToUpdate);
        return patch("/" + itemId, ownerId, itemToUpdate);
    }

    public ResponseEntity<Object> getById(int itemId, int ownerId) {
        log.debug("GET /items/{} userId={}", itemId, ownerId);
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getItemsOfUser(int ownerId) {
        log.debug("GET /items userId={}", ownerId);
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(String text) {
        log.debug("GET /items/search text={}", text);
        return get("/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(CreateCommentDTO dto, int userId, int itemId) {
        log.debug("POST /items/{}/comment userId={} body={}", itemId, userId, dto);
        return post("/" + itemId + "/comment", userId, dto);
    }
}