package net.cinemaApplication.backend.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Represents a ticket that user purchases to watch a movie")
public class Ticket {
    @Schema(description = "The unique identifier of the ticket")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Price of the ticket. Is the same as movie session price")
//    @JsonIgnore
    @Min(value = 1, message = "Price must be positive")
    @Column(name = "ticket_price")
    private double ticketPrice;

    @Schema(description = "Owner of the ticket")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Represents a movie session this ticket is related to")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id")
    private MovieSession session;

    @Schema(description = "Represents a seat in the cinema hall that this ticket has purchased to")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    public void setPrice() {
        if (session != null) {
            this.ticketPrice = session.getMovieSessionPrice();
        }
    }
}
