package net.cinemaApplication.backend.entity.cinemaHall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

import java.util.ArrayList;
import java.util.List;
//firstly add cinema hall and then seats
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cinema_halls")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hall_nr")
    private int hallNr;

    @Column(name = "seat_rows")
    private int seatRows;

    @Column(name = "seat_columns")
    private int seatColumns;

    @JsonIgnore
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<MovieSession> sessions = new ArrayList<>();

    @JsonIgnore
    @Nullable
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<Seat> seats = new ArrayList<>();
}
