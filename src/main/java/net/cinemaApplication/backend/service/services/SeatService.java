package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.List;
import java.util.Optional;

public interface SeatService {
    List<Seat> getAllSeats();
    Optional<Seat> getSeatById(Long id);
    void deleteById(Long id);
    List<Seat> getAllSeatsFromCinemaHall(Long hallId);
    void buyMovieTicketsToGenerateTakenSeats(MovieSession session);
    List<Seat> recommendSeats(int amount, MovieSession session);
}
