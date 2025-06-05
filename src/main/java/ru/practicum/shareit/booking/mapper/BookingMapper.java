package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    ItemMapper itemMapper = new ItemMapper();
    UserMapper userMapper = new UserMapper();

    default Booking toBooking(BookingRequestDto bookingRequestDto, User booker, Item item) {
        if (bookingRequestDto == null && booker == null && item == null) {
            return null;
        }

        Booking booking = new Booking();
        if (bookingRequestDto != null) {
            booking.setStart(bookingRequestDto.getStart());
            booking.setEnd(bookingRequestDto.getEnd());
        }
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    default BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setStatus(booking.getStatus());
        bookingResponseDto.setItem(itemMapper.toItemDto(booking.getItem()));
        bookingResponseDto.setBooker(userMapper.toUserDto(booking.getBooker()));

        return bookingResponseDto;
    }
}