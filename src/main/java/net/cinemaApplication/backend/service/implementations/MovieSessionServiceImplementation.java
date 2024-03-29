package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieFormat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.repository.CinemaHallRepository;
import net.cinemaApplication.backend.repository.MovieRepository;
import net.cinemaApplication.backend.repository.MovieSessionRepository;
import net.cinemaApplication.backend.repository.TicketRepository;
import net.cinemaApplication.backend.service.services.MovieSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieSessionServiceImplementation implements MovieSessionService {
    @Autowired
    private MovieSessionRepository movieSessionRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    /**
     *
     * @return List of all the movie sessions
     */
    @Override
    public List<MovieSession> getAllMovieSessions() {
        return movieSessionRepository.findAll();
    }

    /**
     * Get all movie sessions for a specific date
     * @param date represents a day for what to get all the movie sessions
     * @return all movie sessions for specific date (it filters according to start date)
     */
    @Override
    public List<MovieSession> getAllMovieSessionsForSpecificDate(LocalDate date) {
        List<MovieSession> allMovieSessions = movieSessionRepository.findAll();
        return allMovieSessions.stream().filter(c -> Objects.equals(c.getSessionDate(), date)).toList();
    }

    /**
     * Gets all movie sessions for one week
     * @param date from which the week is counted
     * @return list of all movie sessions for one week
     */
    @Override
    public List<MovieSession> getAllMovieSessionsForAWeek(LocalDate date) {
        List<MovieSession> allMovieSessions = movieSessionRepository.findAll();
        LocalDate plusWeek = date.plusDays(7);
        return allMovieSessions.stream().filter(c -> (c.getSessionDate().isAfter(date) || c.getSessionDate().equals(date))
                && c.getSessionDate().isBefore(plusWeek)).toList();
    }

    /**
     * Update a movie session. You cannot update the cinema hall and price
     * @param movieSession according to which update the old movie session
     * @param id of the movie session to update
     * @return updated movie session
     * @throws ResponseStatusException if movie session not found, session date is past,
     * movie format or language is not correct
     */
    @Override
    public MovieSession updateMovieSession(MovieSession movieSession, Long id) {
        //get old movie session from database
        Optional<MovieSession> movieSessionFromDbOpt = movieSessionRepository.findById(id);
        if (movieSessionFromDbOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie session not found");
        }
        MovieSession movieSessionFromDb = movieSessionFromDbOpt.get();

        if (movieSession.getSessionDate().isAfter(LocalDate.now())) {
            movieSessionFromDb.setSessionDate(movieSession.getSessionDate());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session date is past");
        }
        if (Arrays.stream(MovieFormat.values()).anyMatch(c -> c.equals(movieSession.getMovieFormat()))) {
            movieSessionFromDb.setMovieFormat(movieSession.getMovieFormat());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie format is not correct");
        }
        if (Arrays.stream(Language.values()).anyMatch(c -> c.equals(movieSession.getLanguage()))) {
            movieSessionFromDb.setLanguage(movieSession.getLanguage());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language is not correct");
        }
        return movieSessionRepository.save(movieSessionFromDb);
    }

    /**
     * Deletes a movie session by id
     * @param id for the movie to delete
     * @throws ResponseStatusException if movie session not found
     */
    @Override
    public void deleteById(Long id) {
        if (getMovieSessionById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie session not found");
        }
        movieSessionRepository.deleteById(id);
    }

    /**
     * Gets movie session by id
     * @param id -> finds a session by it
     * @return movie session
     */
    @Override
    public Optional<MovieSession> getMovieSessionById(Long id) {
        return movieSessionRepository.findById(id);
    }

    /**
     * Filters movie sessions for a week according to language
     * @param language to filter by
     * @param date from which the week is counted
     * @return list of filtered movie sessions
     * @throws ResponseStatusException if language is not found
     */
    @Override
    public List<MovieSession> filterByLanguage(Language language, LocalDate date) {
        if (!Arrays.asList(Language.values()).contains(language)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language not found");
        }
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getLanguage() == language).toList();
    }

    /**
     * Filters one week's movie sessions by genre
     * @param genre to filter by
     * @param date from which the week is counted
     * @return list of filtered movie sessions
     * @throws ResponseStatusException if genre is not found
     */

    @Override
    public List<MovieSession> filterByGenre(Genre genre, LocalDate date) {
        if (!Arrays.asList(Genre.values()).contains(genre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre not found");
        }
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getMovie().getGenre() == genre).toList();
    }

    /**
     * Filters one week's movie sessions by start time
     * @param startTime to filter by
     * @param date from which the week is counted
     * @return list of filtered movie sessions
     */
    @Override
    public List<MovieSession> filterByStartTime(LocalTime startTime, LocalDate date) {
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getStartTime().isAfter(startTime)).toList();
    }

    /**
     * Filters one week's movie sessions by age limit
     * @param ageLimit to filter by
     * @param date from which the week is counted
     * @return list of filtered movie sessions
     * @throws ResponseStatusException if genre is not found
     */
    @Override
    public List<MovieSession> filterByAgeLimit(AgeLimit ageLimit, LocalDate date) {
        if (!Arrays.asList(AgeLimit.values()).contains(ageLimit)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Age limit not found");
        }
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getMovie().getAgeLimit() == ageLimit).toList();
    }

    /**
     * Gets all movie sessions for a specific movie
     * @param id represents a movie id
     * @return list of movie sessions
     * @throws ResponseStatusException if movie entity is not found
     */
    @Override
    public List<MovieSession> getMovieSessionsForSpecificMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        return movie.get().getSessions();
    }

    /**
     * Adds new movie session to the movie
     * @param movieId represents a movie to add a new movie session to
     * @param movieSession to add to the system
     * @param cinemaHallId represent a cinema hall where movie session takes place
     * @return new created movie session
     * @throws ResponseStatusException if movie or cinema hall is not found
     */
    @Override
    public MovieSession addNewMovieSessionToTheMovie(Long movieId, MovieSession movieSession, Long cinemaHallId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
        CinemaHall cinemaHall = cinemaHallRepository.findById(cinemaHallId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found"));

        if (movieSession.getSessionDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is past");
        }
        if (Arrays.stream(MovieFormat.values()).noneMatch(c -> c.equals(movieSession.getMovieFormat()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie format is not correct");
        }
        if (Arrays.stream(Language.values()).noneMatch(c -> c.equals(movieSession.getLanguage()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language is not correct");
        }
        if (movieSession.getMovieSessionPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie session price cannot be negative");
        }

        movieSession.setMovie(movie);
        movieSession.setHall(cinemaHall);
        movieSession.calculateEndTime();

        assert cinemaHall.getSeats() != null;
        movieSession.setFreeSeats(cinemaHall.getSeats().size()); //all seats are free when session is created
        MovieSession savedMovieSession = movieSessionRepository.save(movieSession);
        movieRepository.save(movie);
        cinemaHallRepository.save(cinemaHall);

        return savedMovieSession;
    }


    /**
     * Gets all movie sessions for specific date and specific movie (it filters according to start date)
     * @param date to get movie sessions about
     * @param id if the movie to get sessions about
     * @return list of movie sessions
     * @throws ResponseStatusException if movie entity not found
     */
    @Override
    public List<MovieSession> getMovieSessionsForSpecificMovieAndDate(LocalDate date, Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        List<MovieSession> allMovieSessions = movie.get().getSessions();
        assert allMovieSessions != null;
        return allMovieSessions.stream().filter(c -> Objects.equals(c.getSessionDate(), date)).toList();
    }

    /**
     * gets all movie sessions for a week for a specific movie (it filters according to start date)
     * @param date from which the week is counted
     * @param id of the movie
     * @return list of movie sessions
     */
    @Override
    public List<MovieSession> getMovieSessionsForSpecificMovieAndWeek(LocalDate date, Long id) {
        LocalDate plusWeek = date.plusDays(7);
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        List<MovieSession> allMovieSessions = movie.get().getSessions();
        assert allMovieSessions != null;
        return allMovieSessions.stream().filter(c -> (c.getSessionDate().isAfter(date) || c.getSessionDate().equals(date))
                && c.getSessionDate().isBefore(plusWeek)).toList();
    }

}
