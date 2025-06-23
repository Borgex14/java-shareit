package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;


import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ItemMapper {
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "owner", source = "item.owner")
    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "comments", source = "comments")
    ItemDto toFullDto(Item item, BookingShortDto lastBooking,
                      BookingShortDto nextBooking, List<CommentDto> comments);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "owner", source = "owner")
    ItemDto toSimpleDto(Item item);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "bookerId", source = "booker.id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingShortDto toBookingShortDto(Booking booking);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "request", source = "request")
    Item toEntity(ItemDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "request", source = "request")
    @Mapping(target = "owner", ignore = true)
    Item fromCreateDto(ItemCreateDto createDto);

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toDto(Item item);
    ItemResponseDto toResponseDto(Item item);
}