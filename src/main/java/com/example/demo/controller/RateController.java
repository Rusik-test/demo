package com.example.demo.controller;

import com.example.demo.model.RateEntity;
import com.example.demo.service.RateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate")
@CrossOrigin(origins = "*")
public class RateController {

    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/add")
    public RateEntity addRate(@RequestBody RateEntity rateEntity) {
        return rateService.saveRate(rateEntity.getPlayerName(), rateEntity.getRate());
    }
    @GetMapping("/average")
    public double getAverageRate() {
        return rateService.AVGRate();
    }
}
