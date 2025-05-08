package com.example.demo.repository;

import com.example.demo.model.RateEntity;
import com.example.demo.model.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RateRepository extends JpaRepository<RateEntity, Integer>  {
}
