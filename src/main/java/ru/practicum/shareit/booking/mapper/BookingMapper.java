package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toBooking(BookingRequestDto bookingRequestDto, User booker, Item item);

    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    BookingResponseDto toBookingResponseDto(Booking booking);
}