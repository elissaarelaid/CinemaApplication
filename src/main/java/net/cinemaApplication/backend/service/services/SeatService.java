package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatService {
    List<Seat> getAllSeats();

    Optional<Seat> getSeatById(Long id);

    void deleteById(Long id);
    List<Seat> getAllSeatsFromCinemaHall(Long hallId);
}
