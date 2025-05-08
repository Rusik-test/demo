package com.example.demo.service;

import com.example.demo.model.NamesRoomsEntity;
import com.example.demo.repository.NamesRoomsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Service
public class NamesRoomsServiceJPA implements NamesRoomsService {

    private final NamesRoomsRepository namesRoomsRepository;
    public NamesRoomsServiceJPA(NamesRoomsRepository namesRoomsRepository) {
        this.namesRoomsRepository = namesRoomsRepository;
    }

    @Override
    public NamesRoomsEntity saveNames(Long roomId,String playerWhite, String playerBlack) {
        NamesRoomsEntity namesRoomsEntity = new NamesRoomsEntity();
        namesRoomsEntity.setRoomId(roomId);
        namesRoomsEntity.setPlayerWhite(playerWhite);
        namesRoomsEntity.setPlayerBlack(playerBlack);
        return namesRoomsRepository.save(namesRoomsEntity);
    }

    @Override
    public NamesRoomsEntity rewriteSecondPlayer(Long roomId,String playerBlack) {
        Optional<NamesRoomsEntity> optionalRoom = namesRoomsRepository.findById(roomId);
        if (optionalRoom.isPresent()) {
            NamesRoomsEntity room = optionalRoom.get();
            if (!room.getPlayerBlack().equals("1")) {
                throw new IllegalStateException("Другий гравець вже приєднався!");
            }
            room.setPlayerBlack(playerBlack);
            return namesRoomsRepository.save(room);
        } else {
            throw new NoSuchElementException("Кімната з roomId " + roomId + " не знайдена.");
        }
    }

    @Override
    public List<Object[]> sendBackUsersNamesByRoomID(Long roomId) {
        Optional<NamesRoomsEntity> optionalRoom = namesRoomsRepository.findByRoomId(roomId);

        if (optionalRoom.isPresent()) {
            NamesRoomsEntity room = optionalRoom.get();
            return List.<Object[]>of(new Object[]{room.getPlayerWhite(), room.getPlayerBlack()});
        } else {
            throw new NoSuchElementException("Кімната з roomId " + roomId + " не знайдена.");
        }
    }

}
