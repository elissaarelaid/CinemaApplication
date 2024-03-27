package net.cinemaApplication.backend.entity.cinemaHall;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.cinemaApplication.backend.entity.user.Ticket;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seats")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Represents a seat in specific cinema hall")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The unique identifier of the seat")
    private Long id;

    @Schema(description = "Seat number is unique in the cinema hall")
    @Min(value = 1, message = "Seat number be a positive number")
    @Column(name = "seat_nr")
    private int seatNr;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cinema_hall_id")
    @Schema(description = "Cinema hall in which the seat is located")
    private CinemaHall hall;

    @JsonIgnore
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of tickets that are related to this seat")
    List<Ticket> tickets = new ArrayList<>();
}
