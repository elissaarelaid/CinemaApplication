package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.Seat;

import java.util.List;

public interface SeatService {
    List<Seat> getAllSeats();

    Seat updateSeatStatus(Long id, boolean status);

    void deleteById(Long id);
}
