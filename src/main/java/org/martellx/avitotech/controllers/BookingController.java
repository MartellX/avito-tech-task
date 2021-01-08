package org.martellx.avitotech.controllers;

import org.martellx.avitotech.other.ApiAnswer;
import org.martellx.avitotech.entities.Booking;
import org.martellx.avitotech.entities.Room;
import org.martellx.avitotech.repositories.BookingRepository;
import org.martellx.avitotech.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    BookingRepository bookingRepository;
    RoomRepository roomRepository;

    @Autowired
    public BookingController(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @PostMapping(consumes = {"application/x-www-form-urlencoded", "multipart/form-data"})
    public ResponseEntity<?> create(@RequestParam long room_id,
                                    @RequestParam String date_start,
                                    @RequestParam String date_end) throws ParseException {

        Optional<Room> room = roomRepository.findById(room_id);
        if (room.isPresent()) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

            Date startDate = df.parse(date_start);
            Date endDate = df.parse(date_end);

            if (startDate.compareTo(endDate) > 0) {
                return new ApiAnswer(HttpStatus.NOT_ACCEPTABLE ,"Start date cannot be after the end date")
                        .getResponseEntity();
            }
            Booking booking = new Booking();
            booking.setBookingId(0);
            booking.setRoom(room.get());
            booking.setDateStart(startDate);
            booking.setDateEnd(endDate);
            Booking savedBooking = bookingRepository.save(booking);
            return new ResponseEntity<>(Map.of("booking_id", savedBooking.getBookingId()),
                    HttpStatus.CREATED);
        } else {
            return new ApiAnswer(HttpStatus.NOT_FOUND ,"No room found with this id")
                    .getResponseEntity();
        }



    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam long booking_id) {

        try {
            bookingRepository.deleteById(booking_id);
            return new ApiAnswer(HttpStatus.OK, "Deleted successfully").getResponseEntity();
        } catch (EmptyResultDataAccessException e) {
            return new ApiAnswer(HttpStatus.NOT_FOUND ,"No booking found with this id")
                    .getResponseEntity();
        }

    }

    @GetMapping
    public ResponseEntity<?> get(@RequestParam long room_id, @RequestParam(defaultValue = "false") boolean isDescending) {
        ResponseEntity<?> responseEntity;

        try {
            if (roomRepository.existsById(room_id)) {
                List<Booking> result;
                if (isDescending) {
                    result = bookingRepository.findAllByRoom_RoomIdOrderByDateStartDesc(room_id);
                } else {
                    result = bookingRepository.findAllByRoom_RoomIdOrderByDateStartAsc(room_id);
                }

                responseEntity = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                responseEntity = new ApiAnswer(HttpStatus.NOT_FOUND ,"No room found with this id")
                        .getResponseEntity();
            }

        } catch (EmptyResultDataAccessException e) {
            responseEntity = new ApiAnswer(HttpStatus.NOT_FOUND, e.getLocalizedMessage())
                    .getResponseEntity();
        }
        return responseEntity;
    }
}
