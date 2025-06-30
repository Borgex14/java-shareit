package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = {"requestor"})
    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findAllByRequestorId(Long requestorId);

    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User requestor);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id != :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllExceptUser(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE ItemRequest SET description = :description WHERE id = :id")
    void updateDescription(@Param("id") long id, @Param("description") String description);
}
