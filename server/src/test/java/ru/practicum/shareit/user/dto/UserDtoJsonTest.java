package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class UserDtoJsonTest {

    private JacksonTester<UserDto> json;

    @BeforeEach
    void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testUserDtoJson() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Тестовый пользователь")
                .email("test@example.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Тестовый пользователь");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void testUserDtoWithNullValuesJson() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }
}