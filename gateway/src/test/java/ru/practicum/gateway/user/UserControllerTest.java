package ru.practicum.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.gateway.user.dto.UserCreateDto;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    // Test GET /users
    @Test
    void getAllUsers_ShouldCallClientAndReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userClient.getAllUsers()).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk());
    }

    // Test GET /users/{userId}
    @Test
    void getUserById_WithValidId_ShouldReturnOk() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userClient.getUserById(userId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andExpect(status().isOk());
    }

    // Test DELETE /users/{userId}
    @Test
    void deleteUser_ShouldCallClientAndReturnResponse() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        Mockito.when(userClient.deleteUser(userId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    // Test POST /users with valid data
    @Test
    void createUser_WithValidData_ShouldReturnCreated() throws Exception {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        Mockito.when(userClient.createUser(Mockito.any(UserCreateDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());
    }

    // Test PATCH /users/{userId}
    @Test
    void updateUser_ShouldCallClientAndReturnOk() throws Exception {
        long userId = 1L;
        UserCreateDto updateDto = new UserCreateDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userClient.updateUser(userId, updateDto)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
}