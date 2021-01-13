DROP TABLE IF EXISTS rooms CASCADE;
DROP SEQUENCE IF EXISTS rooms_id_sequence CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP SEQUENCE IF EXISTS bookings_id_sequence CASCADE;

-- DROP SCHEMA IF EXISTS public CASCADE;

CREATE TABLE IF NOT EXISTS rooms(
    room_id BIGINT PRIMARY KEY ,
    description text ,
    cost numeric NOT NULL,
    date_added timestamp
);

CREATE SEQUENCE IF NOT EXISTS rooms_id_sequence START WITH 1 INCREMENT BY 1;
CREATE INDEX room_index ON rooms(room_id);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT PRIMARY KEY ,
    room_id BIGINT REFERENCES rooms(room_id) ON DELETE CASCADE ,
    date_start date NOT NULL ,
    date_end date NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS bookings_id_sequence START WITH 1 INCREMENT BY 1;
CREATE INDEX booking_index ON bookings(booking_id);
