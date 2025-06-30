package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.state.BookingFilterState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.state.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Actions;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = getUserOrThrow(userId, Actions.TO_VIEW);
        Item item = getItemOrThrow(bookingRequestDto.getItemId(), Actions.TO_VIEW);

        validateBookingCreation(userId, item, bookingRequestDto);

        if (isTimeOverlaps(bookingRequestDto.getItemId(), bookingRequestDto.getStart(), bookingRequestDto.getEnd())) {
            throw new ValidationException("Товар уже забронирован");
        }

        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(savedBooking);
    }

    private User getUserOrThrow(long userId, String message) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("Пользователя с id = %d для %s не найдено", userId, message));
        }
        return optionalUser.get();
    }

    private Item getItemOrThrow(long itemId, String message) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException(String.format("Вещи с id = %d для %s не найдено", itemId, message));
        }
        return optionalItem.get();
    }

    private void validateBookingCreation(Long userId, Item item, BookingRequestDto bookingRequestDto) {
        LocalDateTime now = LocalDateTime.now();

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(item.getId());
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new BookingOwnItemException();
        }

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) ||
                bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new InvalidDateTimeException("End date must be after start date");
        }

        if (bookingRequestDto.getStart().isBefore(now)) {
            throw new InvalidDateTimeException("Start date must be in the future");
        }

        if (bookingRequestDto.getEnd().isBefore(now)) {
            throw new InvalidDateTimeException("End date must be in the future");
        }
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("User with id " + userId + " is not the owner of the item");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingAlreadyProcessedException();
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(updatedBooking);
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Only booker or owner can view booking details");
        }

        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
        public List<BookingResponseDto> getUserBookings(Long userId, BookingFilterState state, Integer from, Integer size) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        userId, now, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, page);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, page);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, BookingFilterState state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        PageRequest page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, page);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, now, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, now, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, page);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private List<CommentDto> getCommentDtosByItemId(long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapCommentToDto)
                .toList();
    }

    public boolean isTimeOverlaps(Long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> approvedBookings = bookingRepository
                .findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED);

        for (Booking existingBooking : approvedBookings) {
            boolean isNotOverlapping = existingBooking.getEnd().isBefore(start)
                    || existingBooking.getStart().isAfter(end);

            if (!isNotOverlapping) {
                return true;
            }
        }

        return false;
    }

}