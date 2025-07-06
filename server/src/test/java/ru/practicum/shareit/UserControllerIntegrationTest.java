package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;
    private UserDto userDto;
    private UserCreateDto userCreateDto;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .name("Test User 1")
                .email("test1@example.com")
                .build());

        user2 = userRepository.save(User.builder()
                .name("Test User 2")
                .email("test2@example.com")
                .build());

        userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("new@example.com");

        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Updated User");
        userCreateDto.setEmail("updated@example.com");
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
        userRepository.deleteAll();
    }

    @Test
    void createUser_shouldCreateNewUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void createUser_shouldReturnConflictWhenEmailExists() throws Exception {
        userDto.setEmail(user1.getEmail());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict());

        entityManager.clear();
    }

    @Test
    void updateUser_shouldUpdateExistingUser() throws Exception {
        mockMvc.perform(patch("/users/{userId}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userCreateDto.getName())))
                .andExpect(jsonPath("$.email", is(userCreateDto.getEmail())));
    }

    @Test
    void updateUser_shouldUpdateOnlyName() throws Exception {
        userCreateDto.setEmail(null);
        mockMvc.perform(patch("/users/{userId}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userCreateDto.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void updateUser_shouldUpdateOnlyEmail() throws Exception {
        userCreateDto.setName(null);
        mockMvc.perform(patch("/users/{userId}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(userCreateDto.getEmail())));
    }

    @Test
    void updateUser_shouldReturnNotFoundForNonExistingUser() throws Exception {
        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/users/{userId}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void getUser_shouldReturnNotFoundForNonExistingUser() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(user2.getId().intValue())));
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsers() throws Exception {
        userRepository.deleteAll();
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void deleteUser_shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{userId}", user1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnNotFoundForNonExistingUser() throws Exception {
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}