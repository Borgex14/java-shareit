package ru.practicum.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.CreateItemRequestDto;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
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
    public ResponseEntity<Object> getAllUserRequests(
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.getAllUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable long requestId,
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