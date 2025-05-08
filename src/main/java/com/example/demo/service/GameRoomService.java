package com.example.demo.service;
import java.util.List;
import java.util.Random;

import com.example.demo.model.GameRoom;
import org.springframework.stereotype.Service;
import com.example.demo.repository.GameRoomRepository;
@Service
public class GameRoomService {
    private final GameRoomRepository repository;
    private final Random random = new Random();

    public GameRoomService(GameRoomRepository repository) {
        this.repository = repository;
    }

    public GameRoom createRoom(GameRoom room) {
        long randomId;
        do {
            randomId = 100000 + random.nextInt(900000);
        } while (repository.existsById(randomId));

        room.setId(randomId);
        return repository.save(room);
    }
    public GameRoom[] getAllIdRooms() {
        List<GameRoom> rooms = repository.findAll();
        return rooms.toArray(new GameRoom[0]);
    }
}


