package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class ItemRequestDtoJsonTest {

    private JacksonTester<ItemRequestDto> json;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testItemRequestDtoJson() throws IOException {
        ItemRequestCreateDto item1 = ItemRequestCreateDto.builder()
                .id(1L)
                .name("Вещь 1")
                .ownerId(123L)
                .build();

        ItemRequestCreateDto item2 = ItemRequestCreateDto.builder()
                .id(2L)
                .name("Вещь 2")
                .ownerId(123L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна новая вещь")
                .requestorId(10L)
                .created(LocalDateTime.parse("2023-12-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .items(List.of(item1, item2))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна новая вещь");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-12-01T10:00:00");

        // Проверка items (убраны проверки несуществующих полей)
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Вещь 1");
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("Вещь 2");
    }

    @Test
    void testItemRequestDtoWithNullValuesJson() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description(null)
                .requestorId(0L)
                .created(null)
                .items(null)
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.description").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(0);
        assertThat(result).extractingJsonPathValue("$.created").isNull();
        assertThat(result).extractingJsonPathValue("$.items").isNull();
    }
}