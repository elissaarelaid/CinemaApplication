package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieFormat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.repository.MovieRepository;
import net.cinemaApplication.backend.service.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieServiceImplementation implements MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Override
    public Movie addMovie(Movie movie) {
        if (Objects.isNull(movie.getTitle()) || "".equalsIgnoreCase(movie.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie title is not correct");
        }
        if (movie.getMovieLength() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie length is negative");
        }
        if (Arrays.stream(AgeLimit.values()).noneMatch(c -> c.equals(movie.getAgeLimit()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age limit is not correct");
        }
        if (Objects.isNull(movie.getDescription())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie description is null");
        }
        if (Objects.isNull(movie.getDirector())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie director is null");
        }
        if (Arrays.stream(Genre.values()).noneMatch(c -> c.equals(movie.getGenre()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre is not correct");
        }
        if (movie.getRating() < 1 || movie.getRating() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie rating is not correct");
        }
        return movieRepository.save(movie);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override //ToDo: should throw an exception when something is wrong
    public Movie updateMovie(Movie movie, Long id) {
        Movie movieFromDB = movieRepository.findById(id).get(); //get old movie from database

        if (Objects.nonNull(movie.getTitle()) && !"".equalsIgnoreCase(movie.getTitle())) {
            movieFromDB.setTitle(movie.getTitle()); //set movie title as new title
        }
        if (movie.getMovieLength() > 0) {
            movieFromDB.setMovieLength(movie.getMovieLength());
        }
        if (Arrays.stream(AgeLimit.values()).anyMatch(c -> c.equals(movie.getAgeLimit()))) {
            movieFromDB.setAgeLimit(movie.getAgeLimit());
        }
        if (Objects.nonNull(movie.getDescription())) {
            movieFromDB.setDescription(movie.getDescription());
        }
        if (Objects.nonNull(movie.getDirector())) {
            movieFromDB.setDirector(movie.getDirector());
        }
        if (Arrays.stream(Genre.values()).anyMatch(c -> c.equals(movie.getGenre()))) {
            movieFromDB.setGenre(movie.getGenre());
        }
        if (movie.getRating() > 0 && movie.getRating() < 11) {
            movieFromDB.setRating(movie.getRating());
        }
        return movieRepository.save(movieFromDB);
    }

    @Override
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public List<MovieSession> getMovieSessionsForSpecificMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return movie.get().getSessions();
    }

    @Override //add new movie session to the movie
    public MovieSession addNewMovieSessionToTheMovie(Long id, MovieSession movieSession) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        if (movieSession.getSessionDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is past");
        }
        if (Arrays.stream(MovieFormat.values()).noneMatch(c -> c.equals(movieSession.getMovieFormat()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie format is not correct");
        }
        if (Arrays.stream(Language.values()).noneMatch(c -> c.equals(movieSession.getLanguage()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language is not correct");
        }
        movieSession.setMovie(movie);
        movie.getSessions().add(movieSession);
        movieSession.calculateEndTime();
        movieRepository.save(movie);
        return movieSession;
    }

    @Override //return all movie sessions for specific date and specific movie (it filters according to start date)
    public List<MovieSession> getMovieSessionsForSpecificMovieAndDate(LocalDate date, Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        List<MovieSession> allMovieSessions = movie.get().getSessions();
        assert allMovieSessions != null;
        return allMovieSessions.stream().filter(c -> Objects.equals(c.getSessionDate(), date)).toList();
    }
}
