package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.state.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemCreateDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = ItemMapper.fromCreateDto(createDto);
        item.setOwner(user);

        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Название вещи не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание вещи не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new IllegalArgumentException("Статус доступности вещи должен быть указан");
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toDto(savedItem, null, null, Collections.emptyList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = findItemById(itemId);

        boolean hasBooked = bookingRepository.existsApprovedBookingForUserAndItem(
                userId,
                itemId,
                LocalDateTime.now());

        if (!hasBooked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Вы не можете оставить комментарий к вещи, которую не арендовали");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return convertToDto(commentRepository.save(comment));
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
                .map(ItemMapper::toBookingShortDto)
                .orElse(null);

        BookingShortDto nextBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
                        updatedItem.getId(),
                        ownerId,
                        List.of(BookingStatus.APPROVED),
                        now)
                .map(ItemMapper::toBookingShortDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ItemMapper.toDto(updatedItem, lastBooking, nextBooking, comments);
    }

    private CommentDto convertToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId) {
        Item item = findItemById(itemId);

        LocalDateTime now = LocalDateTime.now();
        BookingShortDto lastBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartBeforeOrderByStartDesc(
                        item.getId(),
                        item.getOwner().getId(),
                        List.of(BookingStatus.APPROVED),
                        now)
                .map(ItemMapper::toBookingShortDto)
                .orElse(null);

        BookingShortDto nextBooking = bookingRepository
                .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
                        item.getId(),
                        item.getOwner().getId(),
                        List.of(BookingStatus.APPROVED),
                        now)
                .map(ItemMapper::toBookingShortDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ItemMapper.toDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsByItem = commentRepository.findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(this::convertToDto, Collectors.toList())
                ));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    BookingShortDto lastBooking = bookingRepository
                            .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartBeforeOrderByStartDesc(
                                    item.getId(),
                                    ownerId,
                                    List.of(BookingStatus.APPROVED),
                                    now)
                            .map(ItemMapper::toBookingShortDto)
                            .orElse(null);

                    BookingShortDto nextBooking = bookingRepository
                            .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
                                    item.getId(),
                                    ownerId,
                                    List.of(BookingStatus.APPROVED),
                                    now)
                            .map(ItemMapper::toBookingShortDto)
                            .orElse(null);

                    return ItemMapper.toDto(
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
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItems(text.toLowerCase()).stream()
                .map(item -> ItemMapper.toDto(item, null, null, Collections.emptyList()))
                .collect(Collectors.toList());
    }
}