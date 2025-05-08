package com.example.demo.service;

import com.example.demo.model.RateEntity;

public interface RateService {
    RateEntity saveRate(String playerName, int rate);
    double AVGRate();
}
