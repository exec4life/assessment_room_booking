package com.user.arb.jpa.repository;

import com.user.arb.jpa.entity.Schedule;
import com.user.arb.jpa.entity.ScheduleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleDetailRepository extends JpaRepository<ScheduleDetail, Long> {

    @Query("SELECT bd FROM ScheduleDetail bd JOIN bd.schedule b JOIN b.room r " +
            " WHERE r.id = ?1" +
            " AND ((bd.startTime >= ?2 AND bd.startTime <= ?3) " +
            " OR (bd.endTime >= ?2 AND bd.endTime <= ?3)" +
            " OR (bd.startTime <= ?2 AND bd.endTime >= ?3))")
    List<ScheduleDetail> findByDateRangeOfRoom(Long roomId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT bd FROM ScheduleDetail bd JOIN bd.schedule b JOIN b.room r " +
            " WHERE r.id = ?1 AND b.id != ?2" +
            " AND ((bd.startTime >= ?3 AND bd.startTime <= ?4) " +
            " OR (bd.endTime >= ?3 AND bd.endTime <= ?4)" +
            " OR (bd.startTime <= ?3 AND bd.endTime >= ?4))")
    List<ScheduleDetail> findByDateRangeOfRoomAndNotInSchedule(Long roomId, Long scheduleId, LocalDateTime startTime, LocalDateTime endTime);

    void deleteBySchedule(Schedule schedule);
}
