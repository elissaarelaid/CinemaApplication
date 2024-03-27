package net.cinemaApplication.backend.entity.movieSession;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.user.Ticket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_sessions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Represents a movie session that clients can buy tickets to. Is related to one movie")
public class MovieSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier of the movie session")
    private Long id;

    @Schema(description = "Represents a movie that this session is related to")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Schema(description = "The date when the movie session takes place")
    @Column(name = "session_date")
    private LocalDate sessionDate;

    @Schema(description = "Start time of the movie session")
    @Column(name = "start_time")
    private LocalTime startTime;

    @Schema(description = "End time of the movie session. Is calculated according to movie length", readOnly = true)
    @Column(name = "end_time")
    private LocalTime endTime;

    @Schema(description = "Number of free seats, is updated when someone purchases a ticket", readOnly = true)
    @Min(value = 0, message = "Value must be positive or zero")
    @Column(name = "free_seats")
    private int freeSeats;

    @Schema(description = "Movie session format. Examples: TWO_D, THREE_D")
    @Column(name = "movie_format")
    @Enumerated(EnumType.STRING)
    private MovieFormat movieFormat;

    @Schema(description = "Movie session format. Examples: ESTONIAN, ENGLISH, RUSSIAN")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Schema(description = "Price for a movie session")
    @Min(value = 1, message = "Price must be positive")
    @Column(name = "movie_session_price")
    private int movieSessionPrice;

    @Schema(description = "Represents a cinema hall where the session takes place")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cinema_hall_id")
    private CinemaHall hall;

    @Schema(description = "All the purchased tickets for this session")
    @JsonIgnore
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
