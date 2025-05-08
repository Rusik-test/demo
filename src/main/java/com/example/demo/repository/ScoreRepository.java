package com.example.demo.repository;

import com.example.demo.model.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    @Query("SELECT s.playerName, COUNT(s) FROM ScoreEntity s WHERE s.type = 'win' GROUP BY s.playerName ORDER BY COUNT(s) DESC")
    List<Object[]> countWins();

}
