package com.example.demo.service;

import com.example.demo.model.ScoreEntity;
import com.example.demo.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceJPA implements ScoreService {

    private final ScoreRepository repository;

    public ScoreServiceJPA(ScoreRepository repository) {
        this.repository = repository;
    }

    @Override
    public ScoreEntity saveScore(String playerName, String type) {
        ScoreEntity score = new ScoreEntity();
        score.setPlayerName(playerName);
        score.setType(type);
        return repository.save(score);
    }
    @Override
    public List<Object[]> getWinCounts() {
        return repository.countWins();
    }
}
