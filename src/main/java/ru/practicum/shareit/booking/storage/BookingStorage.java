package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDatesOfItem;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Integer> {
    String BASE_QUERY = """
            select b from Booking b
            join fetch b.booker
            join fetch b.item
            """;

    @Query(BASE_QUERY + """
            where b.booker.id = ?1
            order by b.start desc
            """)
    List<Booking> findByBookerId(int bookerId);

    @Query(BASE_QUERY + """
            where b.booker.id = ?1 and b.status = ?2
            order by b.start desc
            """)
    List<Booking> findByBookerIdAndStatus(int bookerId, BookingStatus status);

    @Query(BASE_QUERY + """
            where b.booker.id = ?1
              and b.status = ?2
              and b.start <= ?3
              and b.end > ?3
            order by b.start desc
            """)
    List<Booking> findCurrentByBookerIdAndStatus(int bookerId, BookingStatus status, LocalDateTime now);

    @Query(BASE_QUERY + """
            where b.booker.id = ?1
              and b.status = ?2
              and b.start > ?3
            order by b.start desc
            """)
    List<Booking> findFutureByBookerIdAndStatus(int bookerId, BookingStatus status, LocalDateTime now);

    @Query(BASE_QUERY + """
            where b.booker.id = ?1
              and b.status = ?2
              and b.end < ?3
            order by b.start desc
            """)
    List<Booking> findPastByBookerIdAndStatus(int bookerId, BookingStatus status, LocalDateTime now);

    @Query(BASE_QUERY + """
            where b.item.ownerId = ?1
            order by b.start desc
            """)
    List<Booking> findByItemOwnerId(int ownerId);

    @Query(BASE_QUERY + """
            where b.item.ownerId = ?1 and b.status = ?2
            order by b.start desc
            """)
    List<Booking> findByItemOwnerIdAndStatus(int ownerId, BookingStatus status);

    @Query(BASE_QUERY + """
            where b.item.ownerId = ?1
              and b.status = ?2
              and b.start <= ?3
              and b.end > ?3
            order by b.start desc
            """)
    List<Booking> findCurrentByItemOwnerIdAndStatus(int ownerId, BookingStatus status, LocalDateTime now);

    @Query(BASE_QUERY + """
            where b.item.ownerId = ?1
              and b.status = ?2
              and b.start > ?3
            order by b.start desc
            """)
    List<Booking> findFutureByItemOwnerIdAndStatus(int ownerId,BookingStatus status, LocalDateTime now);

    @Query(BASE_QUERY + """
            where b.item.ownerId = ?1
              and b.status = ?2
              and b.end < ?3
            order by b.start desc
            """)
    List<Booking> findPastByItemOwnerIdAndStatus(int ownerId, BookingStatus status, LocalDateTime now);

    @Query("""
            select b.item.id as itemId,
            max(case when b.end <= ?2 then b.start else null end) as lastStart,
            min(case when b.start > ?2 then b.start else null end) as nextStart
            from Booking b
            where b.item.id in ?1
                and b.status = ?3
              group by b.item.id
            """)
    List<BookingDatesOfItem> getLastAndNextBookingDatesOfItems(
            List<Integer> itemIds,
            LocalDateTime now,
            BookingStatus status
    );

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            int bookerId, int itemId, BookingStatus status, LocalDateTime now);
}
