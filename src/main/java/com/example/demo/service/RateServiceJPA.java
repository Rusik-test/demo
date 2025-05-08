package com.example.demo.service;

import com.example.demo.model.RateEntity;
import com.example.demo.repository.RateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateServiceJPA implements RateService {

    private final RateRepository rateRepository;

    public RateServiceJPA(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Override
    public RateEntity saveRate(String playerName, int rate) {
        RateEntity entity = new RateEntity();
        entity.setPlayerName(playerName);
        entity.setRate(rate);
        return rateRepository.save(entity);
    }

    @Override
    public double AVGRate() {
        List<RateEntity> rates = rateRepository.findAll();
        return rates.isEmpty() ? 0 :
                rates.stream().mapToInt(RateEntity::getRate).average().orElse(0);
    }
}
