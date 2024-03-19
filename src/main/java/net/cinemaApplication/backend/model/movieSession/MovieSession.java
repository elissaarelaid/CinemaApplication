package net.cinemaApplication.backend.model.movieSession;

import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.model.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.model.movie.Movie;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_sessions")
public class MovieSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //movie loaded only when needed
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "free_seats")
    private int freeSeats;

    @Column(name = "movie_format")
    @Enumerated(EnumType.STRING)
    private MovieFormat movieFormat;

    @Enumerated(EnumType.STRING)
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall hall;
}
