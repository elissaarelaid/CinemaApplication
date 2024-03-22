package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieService {
    Movie addMovie(Movie movie);
    List<Movie> getAllMovies();
    Movie updateMovie(Movie movie, Long id);
    void deleteById(Long id);
    Optional<Movie> getMovieById(Long id);
    List<MovieSession> getMovieSessionsForSpecificMovie(Long id);
    MovieSession addNewMovieSessionToTheMovie(Long id, MovieSession movieSession);

    //return all movie sessions for specific date and specific movie (it filters according to start date)
    List<MovieSession> getMovieSessionsForSpecificMovieAndDate(LocalDate date, Long id);
}
