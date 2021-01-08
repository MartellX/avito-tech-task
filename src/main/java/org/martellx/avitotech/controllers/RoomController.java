package org.martellx.avitotech.controllers;

import org.martellx.avitotech.other.ApiAnswer;
import org.martellx.avitotech.entities.Room;
import org.martellx.avitotech.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    RoomRepository roomRepository;

    @Autowired
    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping(consumes = {"application/x-www-form-urlencoded", "multipart/form-data"})
    public ResponseEntity<?> create(@RequestParam String description, @RequestParam double cost) {
        Room room = new Room();
        room.setRoomId(0);
        room.setDateAdded(new Date());
        room.setDescription(description);
        room.setCost(cost);

        Room saved = roomRepository.save(room);
        return new ResponseEntity<>(Map.of("room_id", saved.getRoomId()), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam long room_id){


        ResponseEntity<?> responseEntity;
        try {
            roomRepository.deleteById(room_id);
            responseEntity = new ApiAnswer(HttpStatus.OK, "Deleted successfully").getResponseEntity();
        } catch (EmptyResultDataAccessException e) {
            responseEntity = new ApiAnswer(HttpStatus.NOT_FOUND, e.getLocalizedMessage()).getResponseEntity();
        }

        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<?> getRooms(@RequestParam(defaultValue = "1") int sorting, @RequestParam(defaultValue = "false") boolean isDescending) {
//        Если параметр "sorting" равен 2, то задается сортировка по дате добавления, в остальных случаях сортировка по цене
        Comparator<Room> comparator;
        if (sorting == 2) {
            comparator = Comparator.comparing(Room::getDateAdded);
        } else {
            comparator = Comparator.comparing(Room::getCost);
        }

        if (isDescending) {
            comparator = comparator.reversed();
        }

        ArrayList<Room> rooms = new ArrayList<>();
        roomRepository.findAll().forEach(rooms::add);
        rooms.sort(comparator);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }
}
