package com.example.demo.controller;

import com.example.demo.model.NamesRoomsEntity;
import com.example.demo.service.NamesRoomsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
public class NamesRoomsController {
    private final NamesRoomsService namesRoomsService;

    public NamesRoomsController(NamesRoomsService namesRoomsService) {
        this.namesRoomsService = namesRoomsService;
    }
    @PostMapping("/writefirstnick")
    public NamesRoomsEntity writeFirstNick(
            @RequestParam Long roomId,
            @RequestParam String firstNick,
            @RequestParam String secondNick
    ) {
        return namesRoomsService.saveNames(roomId, firstNick, secondNick);
    }
    @PostMapping("/writesecondplayer")
    public NamesRoomsEntity writeSecondPlayer(
            @RequestParam Long roomId,
            @RequestParam String secondNick
    ){
        return namesRoomsService.rewriteSecondPlayer(roomId,secondNick);
    }
    @GetMapping("/findnicknamesbyroomid")
        public List<Object[]> findNicknamesbyRoomId(@RequestParam Long roomId){
        return namesRoomsService.sendBackUsersNamesByRoomID(roomId);
    }


}
