package com.user.arb.jpa.repository;

import com.user.arb.jpa.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT b FROM Schedule b JOIN b.room r WHERE r.id = ?1 AND b.active = 1")
    List<Schedule> findByRoom(Long roomId);

    List<Schedule> findByRoomIdIn(List<Long> ids);

    @Query("SELECT DISTINCT(b) FROM Schedule b JOIN b.scheduleDetails bd JOIN b.room r JOIN b.user u " +
            "WHERE u.username LIKE ?1 AND r.id = ?2 AND bd.startTime >= ?3 AND bd.startTime <= ?4 AND b.active = 1")
    Set<Schedule> search(String username, Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}
