package com.user.arb.jpa.repository;

import com.user.arb.jpa.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN b.room r WHERE r.id = ?1 AND b.active = 1")
    List<Booking> findByRoom(Long roomId);

    @Query("SELECT DISTINCT(b) FROM Booking b JOIN b.bookingDetails bd JOIN b.room r JOIN b.user u " +
            "WHERE u.username LIKE ?1 AND r.id = ?2 AND bd.startTime >= ?3 AND bd.startTime <= ?4 AND b.active = 1")
    Set<Booking> search(String username, Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}
