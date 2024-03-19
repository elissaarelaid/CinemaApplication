package net.cinemaApplication.backend.model.cinemaHall;

import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.model.movieSession.MovieSession;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cinema_halls")
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

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MovieSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Seat> seats = new ArrayList<>();
}
