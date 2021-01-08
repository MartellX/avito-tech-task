package org.martellx.avitotech.repositories;

import org.martellx.avitotech.entities.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    List<Booking> findAllByRoom_RoomIdOrderByDateStartAsc(Long room_id);
}
