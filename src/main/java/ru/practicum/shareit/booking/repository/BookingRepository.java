package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.state.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndItemOwnerIdAndStatusInAndStartBeforeOrderByStartDesc(
            Long itemId, Long ownerId, List<BookingStatus> statuses, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndItemOwnerIdAndStatusInAndStartAfterOrderByStartAsc(
            Long itemId, Long ownerId, List<BookingStatus> statuses, LocalDateTime now);

    List<Booking> findByBookerIdAndItemIdAndStatusOrderByStartDesc(
            Long bookerId, Long itemId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndItemOwnerIdAndStatusInOrderByStartDesc(
            Long itemId, Long ownerId, List<BookingStatus> statuses);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusInOrderByStartDesc(
            Long itemId, Long bookerId, List<BookingStatus> statuses);
}