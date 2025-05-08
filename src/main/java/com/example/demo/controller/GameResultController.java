package com.example.demo.controller;


import com.example.demo.model.GameResult;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.GameResultService;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*")
public class GameResultController {
    private final GameResultService service;

    public GameResultController(GameResultService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public GameResult saveResult(@RequestParam String player1,
                                 @RequestParam String type) {
        return service.saveGameResult(player1,type);
    }

    @GetMapping("/all")
    public List<GameResult> getAllResults() {
        return service.getAllResults();
    }
    @GetMapping("/array")
    public GameResult[] getAllResultsArray() {
        return service.getAllResultsAsArray();
    }
}
