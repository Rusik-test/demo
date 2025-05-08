package com.example.demo.repository;

import com.example.demo.model.NamesRoomsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NamesRoomsRepository extends JpaRepository<NamesRoomsEntity,Long> {
    Optional<NamesRoomsEntity> findByRoomId(Long roomId);
}
