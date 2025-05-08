package com.example.demo.service;
import com.example.demo.model.GameResult;
import org.springframework.stereotype.Service;
import com.example.demo.repository.GameResultRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameResultService  {
    private final GameResultRepository repository;

    public GameResultService(GameResultRepository repository) {
        this.repository = repository;
    }

    public GameResult saveGameResult(String player1,String type) {
        GameResult result = new GameResult(player1,type);
        return repository.save(result);
    }

    public List<GameResult> getAllResults() {
        return repository.findAll();
    }
    public GameResult[] getAllResultsAsArray() {
        List<GameResult> list = repository.findAll();
        return list.toArray(new GameResult[0]);
    }
}

