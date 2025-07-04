package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(CreateItemRequestDto itemRequestDto, long userId);

    Collection<ItemRequestDto> getAllItemRequest(long userId);

    ItemRequestDto getItemRequestById(long itemRequestId);

    Collection<ItemRequestDto> getAllItemRequestsFromRequestor(long requesterId);

    ItemRequestDto updateItemRequest(CreateItemRequestDto itemRequestDtoUpdate, long userId, long itemReqId);

    void deleteItemRequest(long itemRequestIdDelete, long userId);
}

