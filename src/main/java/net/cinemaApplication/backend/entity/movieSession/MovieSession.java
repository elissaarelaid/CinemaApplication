package net.cinemaApplication.backend.entity.movieSession;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.user.Ticket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//firstly we add moviesession and later hall
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_sessions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MovieSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) //movie loaded only when needed
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "session_date")
    private LocalDate sessionDate; // The date when the movie session takes place

    @Column(name = "start_time")
    private LocalTime startTime; // Start time of the movie session

    @Column(name = "end_time")
    private LocalTime endTime; // End time of the movie session

    @Column(name = "free_seats")
    private int freeSeats;

    @Column(name = "movie_format")
    @Enumerated(EnumType.STRING)
    private MovieFormat movieFormat;

    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "movie_session_price")
    private int movieSessionPrice;

    @Nullable
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall hall;

    @JsonIgnore
    @Nullable
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<Ticket> tickets = new ArrayList<>();

    public void calculateEndTime() {
        if (this.movie != null && this.startTime != null) {
            int movieLengthInMinutes = this.movie.getMovieLength();
            this.endTime = this.startTime.plusMinutes(movieLengthInMinutes);
        }
    }
}
