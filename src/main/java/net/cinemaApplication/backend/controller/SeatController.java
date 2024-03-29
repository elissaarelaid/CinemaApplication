package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.service.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Tag(name = "Seat", description = "Operations related to seats")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @Operation(summary = "Get all seats",
    description = "Gets all the seats in the system",
    responses = {@ApiResponse(responseCode = "200", description = "Successfully returned all seats")})
    @GetMapping("/seats")
    public List<Seat> getAllSeats()
    {
        return seatService.getAllSeats();
    }

    @Operation(summary = "Get seat by id",
            description = "Gets a seat by id",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully returned a seat by id"),
            @ApiResponse(responseCode = "404", description = "Seat not found")})
    @GetMapping("/seat/{id}")
    public Seat getSeatById(@PathVariable("id") Long id)
    {
        Optional<Seat> seat = seatService.getSeatById(id);
        if (seat.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return seat.get();
    }

    @Operation(summary = "Delete a seat",
            description = "Deletes a seat by id",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully deleted a seat by id"),
                    @ApiResponse(responseCode = "404", description = "Seat not found")})
    @DeleteMapping("/deleteSeat/{id}")
    public String deleteSeatById(@PathVariable("id") Long id)
    {
        seatService.deleteById(id);
        return "Deleted Successfully";
    }

    @Operation(summary = "Get all seats in the cinema hall",
            description = "Gets all the seats from specific cinema hall",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully returned all seats"),
                    @ApiResponse(responseCode = "404", description = "Cinema hall not found")})
    @DeleteMapping("/getCinemaHallSeats/{id}")
    public List<Seat> getAllCinemaHallSeats(@PathVariable("id") Long id)
    {
        return seatService.getAllSeatsFromCinemaHall(id);
    }

}
