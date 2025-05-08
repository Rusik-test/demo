package com.example.demo.controller;

import com.example.demo.model.GameRoom;
import com.example.demo.service.GameRoomService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class GameRoomController {
    private final GameRoomService service;

    public GameRoomController(GameRoomService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public GameRoom createRoom(@RequestBody GameRoom room) {
        System.out.println(room);
        return service.createRoom(room);
    }
    @GetMapping("/idRooms")
    public GameRoom[] getAllIdRooms() {
        return service.getAllIdRooms();
    }

}
