package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.state.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class BookingResponseDtoJsonTest {

    private JacksonTester<BookingResponseDto> json;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testBookingResponseDtoJson() throws IOException {
        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .start(LocalDateTime.parse("2023-12-01T12:53:00", DateTimeFormatter.ISO_DATE_TIME))
                .end(LocalDateTime.parse("2023-12-02T12:53:00", DateTimeFormatter.ISO_DATE_TIME))
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Предмет")
                .description("Описание предмета")
                .available(true)
                .requestId(null)
                .lastBooking(lastBooking)
                .nextBooking(null)
                .comments(null)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Тестовый пример")
                .email("1@abc.com")
                .build();

        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(LocalDateTime.parse("2025-01-01T12:14:16", DateTimeFormatter.ISO_DATE_TIME));
        bookingResponseDto.setEnd(null);
        bookingResponseDto.setStatus(BookingStatus.WAITING);
        bookingResponseDto.setItem(itemDto);
        bookingResponseDto.setBooker(userDto);

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-01-01T12:14:16"); // Проверяем формат даты
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("1@abc.com");
    }
}