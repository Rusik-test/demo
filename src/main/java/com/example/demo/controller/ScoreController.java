package com.example.demo.controller;

import com.example.demo.model.ScoreEntity;
import com.example.demo.service.ScoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/add")
    public ScoreEntity addScore(@RequestParam String playerName, @RequestParam String type) {
        return scoreService.saveScore(playerName, type);
    }

    @GetMapping("/leaderboard")
    public List<Object[]> getLeaderboard() {
        return scoreService.getWinCounts();
    }
}
