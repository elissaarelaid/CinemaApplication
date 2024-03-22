package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieFormat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.repository.MovieSessionRepository;
import net.cinemaApplication.backend.service.services.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MovieSessionServiceImplementation implements MovieSessionService {
    @Autowired
    private MovieSessionRepository movieSessionRepository;
//    @Override
//    public MovieSession saveMovieSession(MovieSession movieSession) {
//        if (movieSession.getEndDate().isBefore(LocalDateTime.now())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is past!");
//        }
//        if (movieSession.getEndDate().isBefore(movieSession.getStartDate())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date should be before end date!");
//        }
//        if (Arrays.stream(MovieFormat.values()).noneMatch(c -> c.equals(movieSession.getMovieFormat()))) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie format is not correct");
//        }
//        if (Arrays.stream(Language.values()).noneMatch(c -> c.equals(movieSession.getLanguage()))) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language is not correct");
//        }
//        return movieSessionRepository.save(movieSession);
//    }

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

    @Override
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
}
