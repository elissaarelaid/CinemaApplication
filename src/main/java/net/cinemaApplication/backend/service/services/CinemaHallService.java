package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.List;

public interface CinemaHallService {
    CinemaHall addCinemaHall(CinemaHall cinemaHall);
    List<CinemaHall> getAllCinemaHalls();
    CinemaHall updateCinemaHall(CinemaHall cinemaHall, Long id);
    void deleteById(Long id);
//
//    //add hall for the movie session, movie session has to exist already
//    MovieSession addHallForTheMovieSession(Long cinemaHallId, Long movieSessionId);

    //add seats to the hall (adds all the seats at one time)
    List<Seat> addSeatsToTheMovieHall(CinemaHall cinemaHall);
}
