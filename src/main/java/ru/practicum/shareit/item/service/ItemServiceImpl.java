package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.state.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
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
        return ItemMapper.toDto(savedItem, null, null);
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

        return ItemMapper.toDto(updatedItem, lastBooking, nextBooking);
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

        return ItemMapper.toDto(item, lastBooking, nextBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        return items.stream()
                .map(item -> {
                    // Получаем последнее завершенное или текущее бронирование
                    BookingShortDto lastBooking = bookingRepository
                            .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartBeforeOrderByStartDesc(
                                    item.getId(),
                                    ownerId,
                                    List.of(BookingStatus.APPROVED),
                                    now)
                            .map(ItemMapper::toBookingShortDto)
                            .orElse(null);

                    // Получаем ближайшее будущее бронирование
                    BookingShortDto nextBooking = bookingRepository
                            .findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
                                    item.getId(),
                                    ownerId,
                                    List.of(BookingStatus.APPROVED),
                                    now)
                            .map(ItemMapper::toBookingShortDto)
                            .orElse(null);

                    return ItemMapper.toDto(item, lastBooking, nextBooking);
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
                .map(item -> ItemMapper.toDto(item, null, null))
                .collect(Collectors.toList());
    }
}