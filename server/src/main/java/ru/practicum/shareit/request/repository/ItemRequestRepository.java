package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT r FROM ItemRequest r JOIN FETCH r.requestor WHERE r.requestor.id = :requestorId")
    List<ItemRequest> findAllByRequestorId(@Param("requestorId") long requestorId);

    @Modifying
    @Query("UPDATE ItemRequest SET description = :description WHERE id = :id")
    void updateDescription(@Param("id") long id, @Param("description") String description);
}
