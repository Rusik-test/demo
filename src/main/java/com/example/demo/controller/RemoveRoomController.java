package com.example.demo.controller;

import com.example.demo.service.RemoveRoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/roomservice")
public class RemoveRoomController {
    private final RemoveRoomService removeRoomService;

    public RemoveRoomController(RemoveRoomService removeRoomService) {
        this.removeRoomService = removeRoomService;
    }
    @PostMapping("/deleteroom")
    public void deleteRoomById(@RequestParam Long roomId) {
        removeRoomService.removeRoomById(roomId);
    }
}
