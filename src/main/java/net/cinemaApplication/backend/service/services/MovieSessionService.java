package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieSessionService {
//    MovieSession saveMovieSession(MovieSession movieSession);
    List<MovieSession> getAllMovieSessions();

    List<MovieSession> getAllMovieSessionsForSpecificDate(LocalDate date);

    //return all movie sessions for one week
    List<MovieSession> getAllMovieSessionsForAWeek(LocalDate date);

    MovieSession updateMovieSession(MovieSession movieSession, Long id);
    void deleteById(Long id);
    Optional<MovieSession> getMovieSessionById(Long id);
}
