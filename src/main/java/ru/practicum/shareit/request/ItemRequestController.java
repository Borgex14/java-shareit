package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestBody @Valid CreateItemRequestDto itemRequestDto,
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemRequestsFromRequestor(
            @RequestHeader(USER_ID_HEADER) long requesterId) {
        log.info("Getting requests for user {}", requesterId);
        return itemRequestService.getAllItemRequestsFromRequestor(requesterId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllItemRequests() {
        return itemRequestService.getAllItemRequest();
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequestById(@PathVariable long itemRequestId) {
        return itemRequestService.getItemRequestById(itemRequestId);
    }

    @PatchMapping("/{itemReqId}")
    public ItemRequestDto updateItemRequest(@RequestBody CreateItemRequestDto itemReqDtoForUpdate,
                                            @RequestHeader(USER_ID_HEADER) long userId,
                                            @PathVariable long itemReqId) {
        return itemRequestService.updateItemRequest(itemReqDtoForUpdate, userId, itemReqId);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteItemRequest(@PathVariable long itemRequestId,
                                            @RequestHeader(USER_ID_HEADER) long userId) {
        itemRequestService.deleteItemRequest(itemRequestId, userId);
    }
}