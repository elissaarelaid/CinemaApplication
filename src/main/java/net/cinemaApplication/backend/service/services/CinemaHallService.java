package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.List;
import java.util.Optional;

public interface CinemaHallService {
    CinemaHall addCinemaHall(CinemaHall cinemaHall);
    List<CinemaHall> getAllCinemaHalls();
    Optional<CinemaHall> getCinemaHallById(Long id);
    void deleteById(Long id);
}
