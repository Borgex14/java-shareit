package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "bookingRequestDto.start", target = "start")
    @Mapping(source = "bookingRequestDto.end", target = "end")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    Booking toBooking(BookingRequestDto bookingRequestDto, User booker, Item item);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "start", target = "start")
    @Mapping(source = "end", target = "end")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    BookingResponseDto toBookingResponseDto(Booking booking);
}