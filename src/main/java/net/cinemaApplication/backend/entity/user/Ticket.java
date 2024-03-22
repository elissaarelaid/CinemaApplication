package net.cinemaApplication.backend.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_price")
    private double ticketPrice;

    @JsonIgnore
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //one ticket is owned by one user

    @JsonIgnore
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MovieSession session;

    @JsonIgnore
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    public void setPrice() {
        if (session != null) {
            this.ticketPrice = session.getMovieSessionPrice();
        }
    }
}
