package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

@Component
public class ItemMapper {

    public static ItemDto toDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        if (item == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                lastBooking,
                nextBooking
        );
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static Item toEntity(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                dto.getOwner(),
                dto.getRequest()
        );
    }

    public static Item fromCreateDto(ItemCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        return new Item(
                null,
                createDto.getName(),
                createDto.getDescription(),
                createDto.getAvailable(),
                createDto.getOwner(),
                createDto.getRequest()
        );
    }
}