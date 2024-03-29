package net.cinemaApplication.backend;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieFormat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.entity.user.User;
import net.cinemaApplication.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

/**
 * Class for generating test data
 */
@Component
public class DataBootstrap implements CommandLineRunner {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieSessionRepository movieSessionRepository;
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public void run(String... args) throws Exception {

        //Movies
        Movie movie1 = createAndSaveMovie("Movie1", Genre.ACTION, AgeLimit.OVER_14, 90, "Cool movie", 9);
        Movie movie2 = createAndSaveMovie("Movie2", Genre.DRAMA,  AgeLimit.OVER_16, 120, "Super movie", 6);
        Movie movie3 = createAndSaveMovie("Movie3", Genre.FANTASY,  AgeLimit.OVER_14, 60, "Sad movie", 7);
        Movie movie4 = createAndSaveMovie("Movie4", Genre.COMEDY, AgeLimit.FAMILY, 105, "Funny movie", 8);
        Movie movie5 = createAndSaveMovie("Movie5", Genre.SCIENCE_FICTION, AgeLimit.NO_LIMIT, 120, "Epic Sci-Fi", 10);

        //Cinema halls
        CinemaHall hall1 = createAndSaveCinemaHall(1, 5, 5);
        CinemaHall hall2 = createAndSaveCinemaHall(2, 10, 11);
        CinemaHall hall3 = createAndSaveCinemaHall(3, 8, 9);
        CinemaHall hall4 = createAndSaveCinemaHall(4, 7, 7);

        //Movie sessions
        createAndSaveMovieSession(movie1, hall1, LocalDate.of(2024, 3, 22), LocalTime.of(14,30), 10, Language.RUSSIAN);
        createAndSaveMovieSession(movie2, hall2, LocalDate.of(2024, 3, 23), LocalTime.of(14,45), 12, Language.RUSSIAN);
        createAndSaveMovieSession(movie3, hall3, LocalDate.of(2024, 3, 24), LocalTime.of(16, 0), 15, Language.ESTONIAN);
        createAndSaveMovieSession(movie4, hall4, LocalDate.of(2024, 3, 25), LocalTime.of(17, 30), 12, Language.ENGLISH);
        createAndSaveMovieSession(movie5, hall1, LocalDate.of(2024, 3, 26), LocalTime.of(19, 0), 18, Language.ESTONIAN);

        //Users
        User user = createAndSaveUser("User1");
        User user2 = createAndSaveUser("User2");
        User user3 = createAndSaveUser("User3");
    }

    private Movie createAndSaveMovie(String title, Genre genre, AgeLimit ageLimit, int movieLength, String description, int rating) {
        Movie movie = Movie.builder()
                .title(title)
                .genre(genre)
                .description(description)
                .movieLength(movieLength)
                .ageLimit(ageLimit)
                .rating(rating)
                .director("Director")
                .build();
        return movieRepository.save(movie);
    }

    private CinemaHall createAndSaveCinemaHall(int hallNr, int seatRows, int seatColumns) {
        CinemaHall hall = CinemaHall.builder()
                .hallNr(hallNr)
                .seatRows(seatRows)
                .seatColumns(seatColumns)
                .build();
        return cinemaHallRepository.save(hall);
    }

    private MovieSession createAndSaveMovieSession(Movie movie, CinemaHall hall, LocalDate date, LocalTime start, int price, Language language) {
        MovieSession session = MovieSession.builder()
                .movie(movie)
                .hall(hall)
                .sessionDate(date)
                .startTime(start)
                .language(language)
                .freeSeats(hall.getSeatRows() * hall.getSeatColumns())
                .movieFormat(MovieFormat.THREE_D)
                .movieSessionPrice(price)
                .build();
        session.calculateEndTime();
        movieSessionRepository.save(session);
        return session;
    }

    private User createAndSaveUser(String name) {
        User user = User.builder().name(name).build();
        userRepository.save(user);
        return user;
    }
}
