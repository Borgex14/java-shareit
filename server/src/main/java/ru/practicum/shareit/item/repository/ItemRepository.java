package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> searchAvailableItems(String text);

    @Query("SELECT i FROM Item i WHERE i.requestId = :requestId")
    List<Item> findAllByRequestId(@Param("requestId") long requestId);

    @Query("SELECT i FROM Item i WHERE i.requestId IN :requestIds")
    List<Item> findAllByRequestIdIn(@Param("requestIds") List<Long> requestIds);

    @Query("SELECT i FROM Item i WHERE i.requestId = :requestId ORDER BY i.id DESC")
    List<Item> findAllByRequestIdOrderByIdDesc(@Param("requestId") long requestId);

    @Query("SELECT i FROM Item i WHERE i.requestId = :requestId AND i.owner.id = :ownerId ORDER BY i.id DESC")
    List<Item> findAllByRequestIdAndOwner(@Param("requestId") long requestId,
                                          @Param("ownerId") long ownerId);
}