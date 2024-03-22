package net.cinemaApplication.backend;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieFormat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.repository.CinemaHallRepository;
import net.cinemaApplication.backend.repository.MovieRepository;
import net.cinemaApplication.backend.repository.MovieSessionRepository;
import net.cinemaApplication.backend.repository.SeatRepository;
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

    @Override
    public void run(String... args) throws Exception {

        Movie movie1 = createAndSaveMovie("Movie1", Genre.ACTION, "Good movie");
        Movie movie2 = createAndSaveMovie("Movie2", Genre.DRAMA, "Better movie");
        Movie movie3 = createAndSaveMovie("Movie3", Genre.FANTASY, "Best movie");

        CinemaHall hall1 = createAndSaveCinemaHall(1, 2, 2);
        CinemaHall hall2 = createAndSaveCinemaHall(2, 3, 5);
        createAndSaveMovieSession(movie1, hall1, LocalDate.of(2024, 3, 22), LocalTime.of(14,30));
        createAndSaveMovieSession(movie2, hall2, LocalDate.of(2024, 3, 23), LocalTime.of(14,45));

        createAndSaveSeats(hall1, 4);
        createAndSaveSeats(hall2, 15);
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

    private void createAndSaveMovieSession(Movie movie, CinemaHall hall, LocalDate date, LocalTime start) {
        MovieSession session = MovieSession.builder()
                .movie(movie)
                .hall(hall)
                .sessionDate(date)
                .startTime(start)
                .language(Language.ENGLISH)
                .freeSeats(hall.getSeatRows() * hall.getSeatColumns())
                .movieFormat(MovieFormat.THREE_D)
                .build();
        session.calculateEndTime();
        movieSessionRepository.save(session);
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
}
