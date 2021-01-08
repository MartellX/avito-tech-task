package org.martellx.avitotech.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import java.util.Date;
import java.util.Locale;

@Entity(name = "booking")
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(name = "booking_id")
    @JsonProperty("booking_id")
    @SequenceGenerator(name = "bookingsIdSeq", sequenceName = "bookings_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookingsIdSeq")
    long bookingId;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    Room room;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    @JsonProperty("date_start")
    Date dateStart;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_end")
    @JsonProperty("date_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    Date dateEnd;

    public Booking(){

    }
    public Booking(long bookingId) {
        this.bookingId = bookingId;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long booking_id) {
        this.bookingId = booking_id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date date_start) {
        this.dateStart = date_start;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date date_end) {
        this.dateEnd = date_end;
    }
}
