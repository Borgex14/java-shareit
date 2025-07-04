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
        getUserOrThrow(userId);
        User requester = userRepository.getReferenceById(userId);

        ItemRequest itemRequest = ItemRequestMapper.fromCreateDto(itemRequestDto, requester);
        ItemRequest savedRequest = requestRepository.save(Objects.requireNonNull(itemRequest));

        return ItemRequestMapper.toDto(savedRequest, Collections.emptyList());
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequest(long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> allRequests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        List<Long> requestIds = allRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = getItemsForListRequests(requestIds);

        Map<Long, List<ItemRequestCreateDto>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(
                        Item::getRequestId,
                        Collectors.mapping(itemMapper::toItemRequestCreateDto, Collectors.toList())
                ));

        return allRequests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())))
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

        List<ItemRequestCreateDto> itemDtos = itemMapper.toItemRequestCreateDtoList(items);

        return ItemRequestMapper.toDto(itemRequest, itemDtos);
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequestsFromRequestor(long requesterId) {
        getUserOrThrow(requesterId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(requesterId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<ItemRequestCreateDto>> itemsByRequestId = getItemsByRequestId(requestIds);

        return requests.stream()
                .map(request -> {
                    List<ItemRequestCreateDto> items = itemsByRequestId.getOrDefault(request.getId(),
                            Collections.emptyList());
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

    private Map<Long, List<ItemRequestCreateDto>> getItemsByRequestId(List<Long> requestIds) {
        if (requestIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(
                        Item::getRequestId,
                        Collectors.mapping(itemMapper::toItemRequestCreateDto, Collectors.toList())
                ));
    }

    private List<Item> getItemsForListRequests(List<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds);
    }
}