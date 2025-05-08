package com.example.demo.service;

import com.example.demo.model.ScoreEntity;

import java.util.List;

public interface ScoreService {
    ScoreEntity saveScore(String playerName, String type);
    List<Object[]> getWinCounts();
}
