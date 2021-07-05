package com.user.arb.jpa.repository;

import com.user.arb.jpa.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.name LIKE ?1")
    Optional<Room> findByName(String name);

    @Query("SELECT r FROM Room r WHERE r.name LIKE ?1 AND r.id != ?2")
    Optional<Room> findByNameInOthers(String name, Long id);

}
