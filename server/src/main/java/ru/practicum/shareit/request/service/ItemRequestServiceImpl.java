package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.enums.Actions;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(CreateItemRequestDto itemRequestDto, long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = requestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(savedRequest, Collections.emptyList());
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequest() {
        List<ItemRequest> allRequests = requestRepository.findAll();
        List<Item> allItems = getItemsForListRequests(
                allRequests.stream().map(ItemRequest::getId).toList()
        );

        return allRequests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        getItemRequestCreateDtoWithOwner(request.getId(), allItems)))
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(long itemRequestId) {
        ItemRequest itemRequest = getItemRequestOrThrow(itemRequestId, Actions.TO_VIEW);

        List<Item> items = itemRepository.findAllByRequestIdOrderByIdDesc(itemRequestId);

        items.forEach(item -> {
            if (item.getOwner() == null) {
                log.error("Item {} has no owner!", item.getId());
            } else {
                log.debug("Item {} owner: {}", item.getId(), item.getOwner().getId());
            }
        });

        if (!items.isEmpty()) {
            log.debug("Last created item: {} with name: {}",
                    items.get(0).getId(), items.get(0).getName());
        }

        List<ItemRequestCreateDto> itemDtos = items.stream()
                .map(itemMapper::toItemRequestCreateDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequestsFromRequestor(long requesterId) {
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return requestRepository.findAllByRequestorOrderByCreatedDesc(user).stream()
                .map(request -> {
                    List<ItemRequestCreateDto> items = itemRepository.findAllByRequestId(request.getId()).stream()
                            .map(itemMapper::toItemRequestCreateDto)
                            .collect(Collectors.toList());

                    items.forEach(dto -> {
                        if (dto.getOwnerId() == null) {
                            log.warn("Item {} has no ownerId in mapping result!", dto.getId());
                        }
                    });

                    return ItemRequestMapper.toDto(request, items);
                })
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto updateItemRequest(CreateItemRequestDto itemReqDtoForUpdate, long userId, long itemReqId) {
        ItemRequest itemReq = getItemRequestOrThrow(itemReqId, Actions.TO_UPDATE);

        if (!itemReq.getRequestor().getId().equals(userId)) {
            throw new AccessError("У вас нет права на редактирование");
        }

        if (itemReqDtoForUpdate.getDescription() != null) {
            ItemRequestMapper.updateFromDto(itemReq, itemReqDtoForUpdate);
            requestRepository.save(itemReq);
        }

        return ItemRequestMapper.toDto(itemReq, getItemRequestCreateDtoWithOwner(itemReqId));
    }

    @Override
    public void deleteItemRequest(long itemRequestId, long userId) {
        ItemRequest itemRequest = getItemRequestOrThrow(itemRequestId, Actions.TO_DELETE);

        if (itemRequest.getRequestor().getId() != userId) {
            throw new AccessError("У вас нет права на удаление");
        }

        requestRepository.deleteById(itemRequestId);
    }

    private ItemRequest getItemRequestOrThrow(long requestId, String message) {
        Optional<ItemRequest> optionalItemRequest = requestRepository.findById(requestId);
        if (optionalItemRequest.isEmpty()) {
            throw new NotFoundException(String.format("Запроса с id = %d для %s не найдено", requestId, message));
        }
        return optionalItemRequest.get();
    }

    private void getUserOrThrow(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new NotFoundException("User with id=" + userId + " not found");
                });
    }

    private List<ItemRequestCreateDto> getItemRequestCreateDtoWithOwner(long requestId) {
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(itemMapper::toItemRequestCreateDto)
                .toList();
    }

    private List<ItemRequestCreateDto> getItemRequestCreateDtoWithOwner(long requestId, List<Item> allItems) {
        List<Item> itemsRequestCreate = allItems.stream()
                .filter(item -> item.getRequestId() == requestId)
                .toList();

        allItems.removeAll(itemsRequestCreate);

        return itemsRequestCreate.stream()
                .map(itemMapper::toItemRequestCreateDto)
                .toList();
    }

    private List<Item> getItemsForListRequests(List<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds);
    }
}