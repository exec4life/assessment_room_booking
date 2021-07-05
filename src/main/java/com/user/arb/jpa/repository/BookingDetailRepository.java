package com.user.arb.jpa.repository;

import com.user.arb.jpa.entity.Booking;
import com.user.arb.jpa.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

    @Query("SELECT bd FROM BookingDetail bd JOIN bd.booking b JOIN b.room r " +
            "WHERE r.id = ?1 AND bd.startTime >= ?2 AND bd.startTime < ?3")
    List<BookingDetail> findByDateRangeOfRoom(Long roomId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT bd FROM BookingDetail bd JOIN bd.booking b JOIN b.room r " +
            "WHERE r.id = ?1 AND b.id != ?2 AND bd.startTime >= ?3 AND bd.startTime < ?4 AND b.active = 1")
    List<BookingDetail> findByDateRangeOfRoomAndNotInBooking(Long roomId, Long bookingId, LocalDateTime startTime, LocalDateTime endTime);

    void deleteByBooking(Booking booking);
}
