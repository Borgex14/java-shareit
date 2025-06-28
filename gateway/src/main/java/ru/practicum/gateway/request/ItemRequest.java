package ru.practicum.gateway.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.gateway.Item.Item;
import ru.practicum.gateway.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "requestor_id")
    private Long requestorId;

    @NotNull
    @Column(name = "created")
    private LocalDateTime created;
}