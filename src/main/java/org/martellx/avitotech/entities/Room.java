package org.martellx.avitotech.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity(name = "room")
@Table(name = "rooms")
public class Room {

    @Id
    @Column(name = "room_id")
    @JsonProperty("room_id")
    @SequenceGenerator(name = "roomsIdSeq", sequenceName = "rooms_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roomsIdSeq")
    Long roomId;

    @Column
    String description;

    @Column
    double cost;

    @JsonIgnore
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_added")
    Date dateAdded;

    @JsonIgnore
    @OneToMany(mappedBy = "room")
    Set<Booking>  bookings;

    public Room(){

    }

    public Room(Long roomId, String description, double cost) {
        this.roomId = roomId;
        this.description = description;
        this.cost = cost;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long id) {
        this.roomId = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date date_added) {
        this.dateAdded = date_added;
    }
}
