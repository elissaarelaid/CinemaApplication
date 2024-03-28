package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieFormat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.repository.CinemaHallRepository;
import net.cinemaApplication.backend.repository.MovieRepository;
import net.cinemaApplication.backend.repository.MovieSessionRepository;
import net.cinemaApplication.backend.service.services.MovieService;
import net.cinemaApplication.backend.service.services.MovieSessionService;
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
    @Autowired
    private MovieSessionService movieSessionService;
    @Autowired
    private MovieSessionRepository movieSessionRepository;

    /**
     * Adds new movie to the system
     * @param movie to add
     * @return new created movie
     * @throws ResponseStatusException if movie title has the wrong length, movie length is negative, age limit is not
     * correct, description length is wrong, movie director's name length is wrong, rating or genre is not correct
     */
    @Override
    public Movie addMovie(Movie movie) {
        if (Objects.isNull(movie.getTitle()) || movie.getTitle().isEmpty() || movie.getTitle().length() > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie title length must be between 1 and 60 characters");
        }
        if (movie.getMovieLength() < 5 || movie.getMovieLength() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie length has to be between 5 and 300 minutes");
        }
        if (Arrays.stream(AgeLimit.values()).noneMatch(c -> c.equals(movie.getAgeLimit()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age limit is not correct");
        }
        if (Objects.isNull(movie.getDescription()) || movie.getDescription().isEmpty() || movie.getDescription().length() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie description length must be between " +
                    "1 and 300 characters");
        }
        if (Objects.isNull(movie.getDirector()) || movie.getDirector().isEmpty() || movie.getDirector().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie director's name length must be between 1 and 50 characters");
        }
        if (Arrays.stream(Genre.values()).noneMatch(c -> c.equals(movie.getGenre()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre is not correct");
        }
        if (Arrays.stream(AgeLimit.values()).noneMatch(c -> c.equals(movie.getAgeLimit()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age limit is not correct");
        }
        if (movie.getRating() < 1 || movie.getRating() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie rating must be between 1 and 10");
        }
        return movieRepository.save(movie);
    }

    /**
     *
     * @return list of all the movies
     */

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Updates an existing movie
     * @param movie according to which to update the movie
     * @param id of the movie to update
     * @return updated movie
     * @throws ResponseStatusException if movie title has the wrong length, movie length is negative, age limit is not
     *      * correct, description length is wrong, movie director's name length is wrong, rating or genre is not correct
     *      OR movie with this id does not exist
     */
    @Override
    public Movie updateMovie(Movie movie, Long id) {
        Optional<Movie> movieFromDBOpt = getMovieById(id); //get old movie from database
        if (movieFromDBOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        Movie movieFromDB = movieFromDBOpt.get();
        if (Objects.nonNull(movie.getTitle()) && movie.getTitle().length() > 1 && movie.getTitle().length() < 60) {
            movieFromDB.setTitle(movie.getTitle());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie title length must be between 1 and 60 characters");
        }
        if (movie.getMovieLength() > 5 && movie.getMovieLength() < 300) {
            movieFromDB.setMovieLength(movie.getMovieLength());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie length has to be between 5 and 300 minutes");
        }
        if (Arrays.stream(AgeLimit.values()).anyMatch(c -> c.equals(movie.getAgeLimit()))) {
            movieFromDB.setAgeLimit(movie.getAgeLimit());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age limit is not correct");
        }
        if (Objects.nonNull(movie.getDescription()) && movie.getDescription().length() > 1 && movie.getDescription().length() < 300) {
            movieFromDB.setDescription(movie.getDescription());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie description length must be between " +
                    "1 and 300 characters");
        }
        if (Objects.nonNull(movie.getDirector()) && movie.getDirector().length() > 1 && movie.getDirector().length() < 50) {
            movieFromDB.setDirector(movie.getDirector());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie director's name length must be between 1 and 50 characters");
        }
        if (Arrays.stream(Genre.values()).anyMatch(c -> c.equals(movie.getGenre()))) {
            movieFromDB.setGenre(movie.getGenre());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre is not correct");
        }
        if (movie.getRating() > 0 && movie.getRating() < 11) {
            movieFromDB.setRating(movie.getRating());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie rating must be between 1 and 10");
        }
        List<MovieSession> movieSessions = movieSessionService.getMovieSessionsForSpecificMovie(id);
        //also changes end time of the movie sessions related to this movie
        for (MovieSession session : movieSessions) {
            session.calculateEndTime();
            movieSessionRepository.save(session);
        }
        return movieRepository.save(movieFromDB);
    }

    /**
     * Deletes a movie by id, also deletes tickets and movie sessions related to it
     * @param id to find a movie to delete
     * @throws ResponseStatusException if system cannot find the movie
     */
    @Override
    public void deleteById(Long id) {
        if (getMovieById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        movieRepository.deleteById(id);
    }

    /**
     * Gets a movie by id
     * @param id to find a movie by
     * @return movie by id
     */
    @Override
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

}
