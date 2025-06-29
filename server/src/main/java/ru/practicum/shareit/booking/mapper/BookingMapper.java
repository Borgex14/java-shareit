package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "start", source = "dto.start")
    @Mapping(target = "end", source = "dto.end")
    @Mapping(target = "status", source = "dto.status")
    @Mapping(target = "item", source = "dto.item")
    @Mapping(target = "booker", source = "dto.booker")
    Booking toBooking(BookingResponseDto dto);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "end", source = "booking.end")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "item.id", source = "booking.item.id")
    @Mapping(target = "item.name", source = "booking.item.name")
    @Mapping(target = "booker.id", source = "booking.booker.id")
    @Mapping(target = "booker.name", source = "booking.booker.name")
    BookingResponseDto toBookingResponseDto(Booking booking);

    default BookingResponseDto toBookingResponseDtoWithComments(Booking booking, List<CommentDto> comments) {
        BookingResponseDto dto = toBookingResponseDto(booking);
        dto.getItem().setComments(comments);
        return dto;
    }

    @Mapping(target = "id", constant = "-1L")
    @Mapping(target = "start", source = "bookingRequestDto.start")
    @Mapping(target = "end", source = "bookingRequestDto.end")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "status", expression = "java(ru.practicum.shareit.booking.state.BookingStatus.WAITING)")
    BookingResponseDto createResponseFromRequestAndDtos(
            BookingRequestDto bookingRequestDto,
            UserDto booker,
            ItemDto item);
}