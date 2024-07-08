package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.service.services.CinemaHallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Cinema Hall", description = "Operations related to cinema halls in the cinema system")
public class CinemaHallController {
    @Autowired
    private CinemaHallService cinemaHallService;

    @Operation(summary = "Get all cinema halls",
            description = "Retrieves a list of all the cinema halls in this cinema",
            responses = {@ApiResponse(responseCode = "200", description = "All cinema halls successfully returned")})
    @GetMapping("/halls")
    public List<CinemaHall> getAllCinemaHalls()
    {
        return cinemaHallService.getAllCinemaHalls();
    }

    @Operation(summary = "Get cinema hall by id",
            description = "Retrieves cinema hall by its id",
            responses = {@ApiResponse(responseCode = "200", description = "Cinema hall successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Cinema hall not found")})
    @GetMapping("/hall/{id}")
    public CinemaHall getCinemaHallById(@PathVariable("id") Long id)
    {
        Optional<CinemaHall> cinemaHall = cinemaHallService.getCinemaHallById(id);
        if (cinemaHall.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found");
        }
        return cinemaHall.get();
    }

    @Operation(summary = "Add new cinema hall object",
            description = "Adds new cinema hall and automatically creates seats to it." +
                    " Ensures hall number, seat columns, and seat rows are positive. Checks if hall number is unique.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "New cinema hall is successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input: Hall number must be positive, " +
                            "Hall nr is already taken, Seat column number must be positive, " +
                            "Seat row number must be positive"),
                    @ApiResponse(responseCode = "404", description = "Cinema hall not found")
            })
    @PostMapping("/add/hall")
    public CinemaHall addCinemaHall(@Valid @RequestBody CinemaHall cinemaHall)
    {
        return cinemaHallService.addCinemaHall(cinemaHall);
    }

    @Operation(summary = "Delete a cinema hall",
    description = "Deletes cinema hall if cinema hall exists. Deletes tickets, seats and movie sessions related to this hall.",
    responses = {@ApiResponse(responseCode = "200", description = "Cinema hall successfully deleted"),
    @ApiResponse(responseCode = "404", description = "Cinema all not found")})
    @DeleteMapping("/deleteCinemaHall/{id}")
    public String deleteCinemaHallById(@PathVariable("id") Long id)
    {
        cinemaHallService.deleteById(id);
        return "Deleted Successfully";
    }

}
