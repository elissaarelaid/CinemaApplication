package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.Seat;

import java.util.List;

public interface SeatService {
    Seat saveSeat(Seat seat);
    List<Seat> getAllSeats();
    Seat updateSeat(Seat seat, Long id);
    void deleteById(Long id);
}
