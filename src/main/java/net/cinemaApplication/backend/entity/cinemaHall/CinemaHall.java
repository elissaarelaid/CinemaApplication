package net.cinemaApplication.backend.entity.cinemaHall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cinema_halls")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Represents a cinema hall within the cinema")

public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier of the cinema hall")
    private Long id;

    @Min(value = 1, message = "Hall number must be a positive number")
    @Column(name = "hall_nr", unique = true)
    @Schema(description = "Unique hall number within the cinema")
    private int hallNr;

    @Min(value = 1, message = "Seat row must be a positive number")
    @Column(name = "seat_rows")
    @Schema(description = "Number of rows in the cinema hall")
    private int seatRows;

    @Min(value = 1, message = "Seat column must be a positive number")
    @Column(name = "seat_columns")
    @Schema(description = "Number of columns in the cinema hall")
    private int seatColumns;

    @JsonIgnore
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Represents a list of movie sessions that take place in this cinema hall")
    List<MovieSession> sessions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Represents a list of seats that are inside a cinema hall")
    List<Seat> seats = new ArrayList<>();
}
