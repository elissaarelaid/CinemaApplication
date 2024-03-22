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

@Component
public class DataBootstrap implements CommandLineRunner {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieSessionRepository movieSessionRepository;
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public void run(String... args) throws Exception {

        Movie movie1 = createAndSaveMovie("Movie1", Genre.ACTION, "Good movie");
        Movie movie2 = createAndSaveMovie("Movie2", Genre.DRAMA, "Better movie");
        Movie movie3 = createAndSaveMovie("Movie3", Genre.FANTASY, "Best movie");

        CinemaHall hall1 = createAndSaveCinemaHall(1, 2, 2);
        CinemaHall hall2 = createAndSaveCinemaHall(2, 3, 5);
        MovieSession session = createAndSaveMovieSession(movie1, hall1, LocalDate.of(2024, 3, 22), LocalTime.of(14,30), 10);
        createAndSaveMovieSession(movie2, hall2, LocalDate.of(2024, 3, 23), LocalTime.of(14,45), 12);

        createAndSaveSeats(hall1, 4);
        createAndSaveSeats(hall2, 15);
        Seat seat = createOneseat(hall1);
        Seat seat2 = createOneseat(hall2);
        User user = createAndSaveUser("User1");

        createAndSaveTickets(session, user, seat);
        createAndSaveTickets(session, user, seat2);
    }

    private Movie createAndSaveMovie(String title, Genre genre, String description) {
        Movie movie = Movie.builder()
                .title(title)
                .genre(genre)
                .description(description)
                .movieLength(120)
                .ageLimit(AgeLimit.NO_LIMIT)
                .rating(8)
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

    private MovieSession createAndSaveMovieSession(Movie movie, CinemaHall hall, LocalDate date, LocalTime start, int price) {
        MovieSession session = MovieSession.builder()
                .movie(movie)
                .hall(hall)
                .sessionDate(date)
                .startTime(start)
                .language(Language.ENGLISH)
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
    private void createAndSaveSeats(CinemaHall hall, int numberOfSeats) {
        for (int i = 1; i <= numberOfSeats; i++) {
            Seat seat = Seat.builder()
                    .seatNr(i)
                    .isSeatTaken(false)
                    .hall(hall)
                    .build();
            seatRepository.save(seat);
        }
    }

    private Seat createOneseat(CinemaHall hall) {
        Seat seat = Seat.builder()
                .seatNr(7)
                .isSeatTaken(false)
                .hall(hall)
                .build();
        seatRepository.save(seat);
        return seat;
    }
    private void createAndSaveTickets(MovieSession session, User user, Seat seat) {
            Ticket ticket = Ticket.builder().user(user).session(session).seat(seat).build();
            ticket.setPrice();
            ticketRepository.save(ticket);
    }
}
