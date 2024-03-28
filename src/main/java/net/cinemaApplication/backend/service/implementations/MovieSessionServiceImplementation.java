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
import net.cinemaApplication.backend.service.services.MovieSessionService;
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

    @Override
    public List<MovieSession> getAllMovieSessions() {
        return movieSessionRepository.findAll();
    }

    @Override //return all movie sessions for specific date (it filters according to start date)
    public List<MovieSession> getAllMovieSessionsForSpecificDate(LocalDate date) {
        List<MovieSession> allMovieSessions = movieSessionRepository.findAll();
        return allMovieSessions.stream().filter(c -> Objects.equals(c.getSessionDate(), date)).toList();
    }

    @Override //return all movie sessions for one week
    public List<MovieSession> getAllMovieSessionsForAWeek(LocalDate date) {
        List<MovieSession> allMovieSessions = movieSessionRepository.findAll();
        LocalDate plusWeek = date.plusDays(7);
        return allMovieSessions.stream().filter(c -> (c.getSessionDate().isAfter(date) || c.getSessionDate().equals(date))
                && c.getSessionDate().isBefore(plusWeek)).toList();
    }

    @Override //cannot update cinemaHall
    public MovieSession updateMovieSession(MovieSession movieSession, Long id) {

        MovieSession movieSessionFromDb = movieSessionRepository.findById(id).get(); //get old movie session from database

        if (movieSession.getSessionDate().isAfter(LocalDate.now())) {
            movieSessionFromDb.setSessionDate(movieSession.getSessionDate());
        }
        if (movieSession.getMovieSessionPrice() > 0) {
            movieSessionFromDb.setMovieSessionPrice(movieSession.getMovieSessionPrice());
        }
        if (Arrays.stream(MovieFormat.values()).noneMatch(c -> c.equals(movieSession.getMovieFormat()))) {
            movieSessionFromDb.setMovieFormat(movieSession.getMovieFormat());
        }
        if (Arrays.stream(Language.values()).noneMatch(c -> c.equals(movieSession.getLanguage()))) {
            movieSessionFromDb.setLanguage(movieSession.getLanguage());
        }
        return movieSessionRepository.save(movieSessionFromDb);
    }

    @Override
    public void deleteById(Long id) {
        movieSessionRepository.deleteById(id);
    }

    @Override
    public Optional<MovieSession> getMovieSessionById(Long id) {
        return movieSessionRepository.findById(id);
    }

    /**
     * Filters movie sessions for a week according to language
     * @param language
     * @return
     */
    @Override
    public List<MovieSession> filterByLanguage(Language language, LocalDate date) {
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getLanguage() == language).toList();
    }

    @Override
    public List<MovieSession> filterByGenre(Genre genre, LocalDate date) {
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getMovie().getGenre() == genre).toList();
    }

    @Override
    public List<MovieSession> filterByStartTime(LocalTime startTime, LocalDate date) {
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getStartTime().isAfter(startTime)).toList();
    }

    @Override
    public List<MovieSession> filterByAgeLimit(AgeLimit ageLimit, LocalDate date) {
        List<MovieSession> movieSessionForAWeek = getAllMovieSessionsForAWeek(date);
        return movieSessionForAWeek.stream().filter(m -> m.getMovie().getAgeLimit() == ageLimit).toList();
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

    @Override //return all movie sessions for a week for a specific movie (it filters according to start date)
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
