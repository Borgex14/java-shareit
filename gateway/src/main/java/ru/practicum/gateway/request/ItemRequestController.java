package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.CreateItemRequestDto;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestBody @Valid CreateItemRequestDto requestDto,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.createItemRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        ResponseEntity<Object> response = itemRequestClient.getAllUserRequests(userId);

        if (response.getStatusCode().isError()) {
            return response;
        }

        Object body = response.getBody();
        if (body instanceof Collection) {
            return ResponseEntity.ok(body);
        }

        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(USER_ID_HEADER) long userId) {

        log.info("Getting all requests for user {}", userId);
        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId);
        log.info("Response: {}", response.getBody());
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @Positive @PathVariable long requestId,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<Object> updateRequest(
            @RequestBody @Valid CreateItemRequestDto requestDto,
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long requestId) {
        return itemRequestClient.updateRequest(requestDto, userId, requestId);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> deleteRequest(
            @PathVariable long requestId,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.deleteRequest(requestId, userId);
    }
}