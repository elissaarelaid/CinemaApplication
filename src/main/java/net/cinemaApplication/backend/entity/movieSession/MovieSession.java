package net.cinemaApplication.backend.entity.movieSession;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.movie.Movie;

import java.time.LocalDateTime;
//firstly we add moviesession and later hall
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

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall hall;
}
