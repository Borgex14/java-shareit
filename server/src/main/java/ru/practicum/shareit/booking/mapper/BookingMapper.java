package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "start", source = "bookingRequestDto.start")
    @Mapping(target = "end", source = "bookingRequestDto.end")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "status", expression = "java(ru.practicum.shareit.booking.state.BookingStatus.WAITING)")
    Booking toBooking(BookingRequestDto bookingRequestDto, User booker, Item item);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "end", source = "booking.end")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "item", source = "booking.item")
    @Mapping(target = "booker", source = "booking.booker")
    BookingResponseDto toBookingResponseDto(Booking booking);
}