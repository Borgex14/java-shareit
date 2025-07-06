package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    @Query("select c from Comment as c where c.item.id in :itemIds")
    List<Comment> findByItemIdIn(List<Long> itemIds);

    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT c FROM Comment c WHERE c.item.id = :itemId")
    List<Comment> findByItemIdWithAuthor(Long itemId);
}