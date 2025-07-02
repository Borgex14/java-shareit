package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(ItemRequestMapper.class)
@Transactional
@ActiveProfiles("test")
public class ItemRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    EntityManager entityManager;

    private User requester;
    private User otherUser;
    private ItemRequest itemRequest;
    private CreateItemRequestDto createItemRequestDto;

    @BeforeEach
    void setUp() {
        requester = userRepository.save(User.builder()
                .name("Requester")
                .email("requester@example.com")
                .build());

        otherUser = userRepository.save(User.builder()
                .name("Other User")
                .email("other@example.com")
                .build());

        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Need item for testing")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build());

        createItemRequestDto = new CreateItemRequestDto();
        createItemRequestDto.setDescription("New request description");
    }

    @AfterEach
    @DirtiesContext
    void tearDown() {
        entityManager.clear();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createItemRequest_shouldCreateNewRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.description", is("New request description")));
    }

    @Test
    void getAllItemRequests_shouldReturnAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", otherUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId().intValue())));
    }

    @Test
    void getItemRequestById_shouldReturnRequest() throws Exception {
        mockMvc.perform(get("/requests/{itemRequestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId().intValue())))
                .andExpect(jsonPath("$.description", is("Need item for testing")));
    }

    @Test
    void updateItemRequest_shouldUpdateRequest() throws Exception {
        CreateItemRequestDto updateDto = new CreateItemRequestDto();
        updateDto.setDescription("Updated description");

        mockMvc.perform(patch("/requests/{itemReqId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Updated description")));
    }

    @Test
    void deleteItemRequest_shouldDeleteRequest() throws Exception {
        mockMvc.perform(delete("/requests/{itemRequestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/{itemRequestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemRequest_shouldReturnForbiddenForNonRequester() throws Exception {
        CreateItemRequestDto updateDto = new CreateItemRequestDto();
        updateDto.setDescription("Updated description");

        mockMvc.perform(patch("/requests/{itemReqId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", otherUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }
}