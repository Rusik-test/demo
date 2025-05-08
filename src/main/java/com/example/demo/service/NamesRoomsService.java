package com.example.demo.service;

import com.example.demo.model.NamesRoomsEntity;

import java.util.List;

public interface NamesRoomsService {
    NamesRoomsEntity saveNames(Long roomId,String playerWhite,String playerBlack);
    NamesRoomsEntity rewriteSecondPlayer( Long roomId,String playerBlack);
    List<Object[]> sendBackUsersNamesByRoomID(Long roomId);
}
