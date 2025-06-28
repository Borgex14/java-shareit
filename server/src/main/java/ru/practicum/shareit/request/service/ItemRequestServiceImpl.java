package ru.practicum.shareit.request.service;

import jakarta.persistence.PersistenceException;
import ru.practicum.shareit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(CreateItemRequestDto itemRequestDto, long userId) {
        try {
            User requester = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            log.debug("Creating request from DTO: {}", itemRequestDto);
            ItemRequest itemRequest = ItemRequestMapper.fromCreateDto(itemRequestDto, requester);
            log.debug("Mapped entity before save: {}", itemRequest);
            ItemRequest saved = requestRepository.save(itemRequest);
            log.debug("Saved entity: {}", saved);
            if(saved == null) {
                throw new PersistenceException("Failed to save request");
            }
            ItemRequestDto dto = ItemRequestMapper.toDto(saved);
            log.debug("Result DTO: {}", dto);

            return dto;
        } catch (DataAccessException e) {
            log.error("Database error while creating item request: {}", e.getMessage());
            throw new ValidationException("Failed to save item request due to database error");
        }
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
                        getItemRequestCreateDto(request.getId(), allItems)))
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(long itemRequestId) {
        ItemRequest itemRequest = getItemRequestOrThrow(itemRequestId, Actions.TO_VIEW);
        return ItemRequestMapper.toDto(itemRequest, getItemRequestCreateDto(itemRequestId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> getAllItemRequestsFromRequestor(long requesterId) {
        log.debug("Checking user {}", requesterId);
        getUserOrThrow(requesterId);

        log.debug("Finding requests for user {}", requesterId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(requesterId);

        log.debug("Found {} requests", requests.size());
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();

        log.debug("Finding items for requests");
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        return requests.stream()
                .map(request -> {
                    List<ItemRequestCreateDto> requestItems = items.stream()
                            .filter(item -> request.getId().equals(item.getRequest().getId()))
                            .map(ItemMapper::toItemRequestCreateDto)
                            .toList();
                    return ItemRequestMapper.toDto(request, requestItems);
                })
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto updateItemRequest(CreateItemRequestDto itemReqDtoForUpdate, long userId, long itemReqId) {
        ItemRequest itemReq = getItemRequestOrThrow(itemReqId, Actions.TO_UPDATE);

        if (!itemReq.getRequestorId().equals(userId)) {
            throw new AccessError("У вас нет права на редактирование");
        }

        if (itemReqDtoForUpdate.getDescription() != null) {
            ItemRequestMapper.updateFromDto(itemReq, itemReqDtoForUpdate);
            requestRepository.save(itemReq);
        }

        return ItemRequestMapper.toDto(itemReq, getItemRequestCreateDto(itemReqId));
    }

    @Override
    public void deleteItemRequest(long itemRequestId, long userId) {
        ItemRequest itemRequest = getItemRequestOrThrow(itemRequestId, Actions.TO_DELETE);

        if (itemRequest.getRequestorId() != userId) {
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
                    return new UserNotFoundException(userId);
                });
    }

    private List<ItemRequestCreateDto> getItemRequestCreateDto(long requestId) {
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(item -> {
                    if (item == null) return null;
                    return ItemMapper.toItemRequestCreateDto(item);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<ItemRequestCreateDto> getItemRequestCreateDto(long requestId, List<Item> allItems) {
        List<Item> itemsRequestCreate = allItems.stream()
                .filter(item -> item.getRequest().getId() == requestId)
                .toList();

        allItems.removeAll(itemsRequestCreate);

        return itemsRequestCreate.stream()
                .map(ItemMapper::toItemRequestCreateDto)
                .toList();
    }

    private List<Item> getItemsForListRequests(List<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds);
    }
}
