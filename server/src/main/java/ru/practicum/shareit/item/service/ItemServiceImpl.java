package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.state.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository, ItemRequestRepository requestRepository, ItemMapper itemMapper, UserMapper userMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        log.info("Adding item for user {}, data: {}", userId, itemDto);
        validateItemData(itemDto);
        User owner = getUserOrThrow(userId);

        if (itemDto.getRequestId() != null) {
            requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Request with id %d not found", itemDto.getRequestId())
                    ));
        }

        UserDto ownerDto = userMapper.toDto(owner);
        itemDto.setOwner(ownerDto);

        Item savedItem = itemRepository.save(itemMapper.toEntity(itemDto));
        log.info("Saved item with ownerId: {}", savedItem.getOwner().getId());

        ItemDto result = itemMapper.toFullDto(savedItem, null, null, Collections.emptyList());
        log.info("Mapped ItemDto owner: {}", result.getOwner());

        return result;
    }

    private Item createItem(Item item, Long ownerId) {
        User owner = getUserOrThrow(ownerId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private void validateItemData(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Item availability must be specified");
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Long bookerId, Long itemId, CommentDto dto) {
        LocalDateTime now = LocalDateTime.now();

        User author = getUserOrThrow(bookerId);
        Item item = getItemOrThrow(itemId);

        validateCommentCreation(bookerId, itemId, now);

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(now)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved with ID: {}, for item ID: {}", savedComment.getId(), itemId);

        boolean exists = commentRepository.existsById(savedComment.getId());
        log.info("Comment exists in DB: {}", exists);

        return CommentMapper.mapCommentToDto(savedComment);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private void validateCommentCreation(Long bookerId, Long itemId, LocalDateTime now) {
        List<Booking> bookings = bookingRepository
                .findByBookerIdAndItemIdAndStatusOrderByStartDesc(bookerId, itemId, BookingStatus.APPROVED);

        log.info("Found bookings for user {} and item {}: {}", bookerId, itemId, bookings);

        boolean canComment = bookings.stream()
                .anyMatch(booking -> {
                    boolean isPast = booking.getEnd().isBefore(now);
                    log.info("Booking {} end {} is before now {}: {}",
                            booking.getId(), booking.getEnd(), now, isPast);
                    return isPast;
                });

        if (!canComment) {
            throw new ValidationException("You can only comment items you've actually booked in the past");
        }
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemCreateDto updateDto) {
        Item existingItem = findItemById(itemId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на изменение этой вещи");
        }

        boolean isUpdated = false;

        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            existingItem.setName(updateDto.getName());
            isUpdated = true;
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            existingItem.setDescription(updateDto.getDescription());
            isUpdated = true;
        }

        if (updateDto.getAvailable() != null) {
            existingItem.setAvailable(updateDto.getAvailable());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new IllegalArgumentException("Нет полей для обновления");
        }

        Item updatedItem = itemRepository.save(existingItem);

        LocalDateTime now = LocalDateTime.now();
        BookingShortDto lastBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartBeforeOrderByStartDesc(
                        updatedItem.getId(),
                        ownerId,
                        List.of(BookingStatus.APPROVED),
                        now)
                .map(itemMapper::toBookingShortDto)
                .orElse(null);

        BookingShortDto nextBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
                        updatedItem.getId(),
                        ownerId,
                        List.of(BookingStatus.APPROVED),
                        now)
                .map(itemMapper::toBookingShortDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapCommentToDto)
                .collect(Collectors.toList());

        return itemMapper.toFullDto(updatedItem, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId, Long ownerId) {
        Item item = findItemById(itemId);
        LocalDateTime now = LocalDateTime.now();

        List<CommentDto> comments = commentRepository.findByItemIdWithAuthor(itemId).stream()
                .map(CommentMapper::mapCommentToDto)
                .collect(Collectors.toList());

        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;

        if (item.getOwner().getId().equals(ownerId)) {
            lastBooking = bookingRepository
                    .findFirstByItemIdAndItemOwnerIdAndStatusInAndEndBeforeOrderByEndDesc(
                            item.getId(),
                            ownerId,
                            List.of(BookingStatus.APPROVED),
                            now)
                    .map(itemMapper::toBookingShortDto)
                    .orElse(null);

            nextBooking = bookingRepository
                    .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
                            item.getId(),
                            ownerId,
                            List.of(BookingStatus.APPROVED),
                            now)
                    .map(itemMapper::toBookingShortDto)
                    .orElse(null);
        }

        log.debug("Returning item with lastBooking: {}, nextBooking: {}", lastBooking, nextBooking);
        return itemMapper.toFullDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<CommentDto>> commentsByItem = commentRepository.findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::mapCommentToDto, Collectors.toList())
                ));

        List<Booking> allBookings = bookingRepository.findByItemIdInAndStatusIn(
                itemIds,
                List.of(BookingStatus.APPROVED)
        );

        Map<Long, List<Booking>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getItem().getId()
                ));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), Collections.emptyList());

                    BookingShortDto lastBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isBefore(now))
                            .max(Comparator.comparing(Booking::getStart))
                            .map(itemMapper::toBookingShortDto)
                            .orElse(null);

                    BookingShortDto nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .map(itemMapper::toBookingShortDto)
                            .orElse(null);

                    return itemMapper.toFullDto(
                            item,
                            lastBooking,
                            nextBooking,
                            commentsByItem.getOrDefault(item.getId(), Collections.emptyList())
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchAvailableItems(text.toLowerCase()).stream()
                .map(item -> itemMapper.toFullDto(item, null, null, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    private List<CommentDto> getCommentDtoToItemDto(long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapCommentToDto)
                .toList();
    }
}