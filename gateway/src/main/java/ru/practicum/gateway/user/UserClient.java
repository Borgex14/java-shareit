package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserCreateDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserCreateDto userDto) {
        HttpHeaders headers = defaultHeaders(null);
        HttpEntity<UserCreateDto> requestEntity = new HttpEntity<>(userDto, headers);
        return rest.postForEntity("", requestEntity, Object.class);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("", -1);
    }

    public ResponseEntity<Object> getUserById(long userId) {
        return get("/" + userId, -1);
    }

    public ResponseEntity<Object> updateUser(long userId, UserCreateDto userDto) {
        return patch("/" + userId, -1, userDto);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/" + userId, -1);
    }
}