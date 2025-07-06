package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class ItemDtoJsonTest {

    private JacksonTester<ItemDto> json;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testItemDtoJson() throws IOException {
        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .start(LocalDateTime.parse("2023-12-01T12:53:00", DateTimeFormatter.ISO_DATE_TIME))
                .end(LocalDateTime.parse("2023-12-02T12:53:00", DateTimeFormatter.ISO_DATE_TIME))
                .build();

        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Владелец")
                .email("owner@example.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Предмет")
                .description("Описание предмета")
                .available(true)
                .owner(owner)
                .requestId(null)
                .lastBooking(lastBooking)
                .nextBooking(null)
                .comments(List.of())
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Предмет");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание предмета");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("owner@example.com");

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo("2023-12-01T12:53:00");

        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }
}