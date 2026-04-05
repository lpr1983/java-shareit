package ru.practicum.shareit.gateway.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.gateway.client.BaseClient;

@Slf4j
@Component
public class UserClient extends BaseClient {
    private static final String BASE_PATH = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BASE_PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(CreateUserDTO newUser) {
        log.debug("POST /users body={}", newUser);
        return post("", newUser);
    }

    public ResponseEntity<Object> getAll() {
        log.debug("GET /users");
        return get("");
    }

    public ResponseEntity<Object> getById(int id) {
        log.debug("GET /users/{}", id);
        return get("/" + id);
    }

    public ResponseEntity<Object> delete(int id) {
        log.debug("DELETE /users/{}", id);
        return delete("/" + id);
    }

    public ResponseEntity<Object> update(int id, UpdateUserDTO userToUpdate) {
        log.debug("PATCH /users/{} body={}", id, userToUpdate);
        return patch("/" + id, userToUpdate);
    }
}