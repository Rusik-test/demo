package com.example.demo.service;

import com.example.demo.repository.GameRoomRepository;
import com.example.demo.repository.NamesRoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoveRoomServiceJPA implements RemoveRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final NamesRoomsRepository namesRoomsRepository;

    @Autowired
    public RemoveRoomServiceJPA(GameRoomRepository gameRoomRepository,
                                NamesRoomsRepository namesRoomsRepository) {
        this.gameRoomRepository = gameRoomRepository;
        this.namesRoomsRepository = namesRoomsRepository;
    }

    @Override
    public void removeRoomById(Long roomId) {
        namesRoomsRepository.findByRoomId(roomId)
                .ifPresent(namesRoomsRepository::delete);

        gameRoomRepository.deleteById(roomId);
    }
}