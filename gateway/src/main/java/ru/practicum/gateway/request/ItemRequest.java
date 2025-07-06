package ru.practicum.gateway.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    @ToString.Exclude
    private User requestor;

    @Column(name = "created", nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private List<Item> items;
}