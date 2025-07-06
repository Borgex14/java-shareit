package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = {"requestor"})
    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);
}
