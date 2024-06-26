package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MovieSessionService {
    List<MovieSession> getAllMovieSessions();

    List<MovieSession> getAllMovieSessionsForSpecificDate(LocalDate date);

    //return all movie sessions for one week
    List<MovieSession> getAllMovieSessionsForAWeek(LocalDate date);

    MovieSession updateMovieSession(MovieSession movieSession, Long id);
    void deleteById(Long id);
    Optional<MovieSession> getMovieSessionById(Long id);
    List<MovieSession> filterByLanguage(Language language, LocalDate date);
    List<MovieSession> filterByGenre(Genre genre, LocalDate date);
    List<MovieSession> filterByStartTime(LocalTime startTime, LocalDate date);
    List<MovieSession> filterByAgeLimit(AgeLimit ageLimit, LocalDate date);
    List<MovieSession> getMovieSessionsForSpecificMovie(Long id);
    MovieSession addNewMovieSessionToTheMovie(Long movieId, MovieSession movieSession, Long cinemaHallId);

    //return all movie sessions for specific date and specific movie (it filters according to start date)
    List<MovieSession> getMovieSessionsForSpecificMovieAndDate(LocalDate date, Long id);

    //return all movie sessions for a week for a specific movie (it filters according to start date)
    List<MovieSession> getMovieSessionsForSpecificMovieAndWeek(LocalDate date, Long id);
}
