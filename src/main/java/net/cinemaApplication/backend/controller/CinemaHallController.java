package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
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
public class CinemaHallController {
    @Autowired
    private CinemaHallService cinemaHallService;

    @Operation(summary = "Get all cinema halls")
    @GetMapping("/cinemaHalls")
    public List<CinemaHall> getAllCinemaHalls()
    {
        return cinemaHallService.getAllCinemaHalls();
    }

    @Operation(summary = "Get cinema hall by id")
    @GetMapping("/cinemaHall{id}")
    public CinemaHall getCinemaHallById(@PathVariable("id") Long id)
    {
        Optional<CinemaHall> cinemaHall = cinemaHallService.getAllCinemaHalls().stream().filter(c -> Objects.equals(c.getId(), id)).findFirst();
        if (cinemaHall.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return cinemaHall.get();
    }

    @Operation(summary = "Add new cinema hall object")
    @PostMapping("/add/cinemaHall")
    public CinemaHall addCinemaHall(@Valid @RequestBody CinemaHall cinemaHall)
    {
        return cinemaHallService.addCinemaHall(cinemaHall);
    }
//    @Operation(summary = "Add cinema hall to the movie session")
//    @PostMapping("/add/cinemaHall{cinemaHallId}/movieSession{movieSessionId}")
//    public MovieSession addCinemaHall(@PathVariable("movieSessionId") Long movieSessionId,
//                                    @PathVariable("cinemaHallId") Long cinemaHallId)
//    {
//        return cinemaHallService.addHallForTheMovieSession(cinemaHallId, movieSessionId);
//    }

//    @Operation(summary = "Add seats to the cinema hall (you can only add as many seats as cinema hall allows)")
//    @PostMapping("/add/cinemaHall{cinemaHallId}/seats")
//    public List<Seat> addCinemaHall(@PathVariable("cinemaHallId") Long cinemaHallId)
//    {
//        return cinemaHallService.addSeatsToTheMovieHall(cinemaHallId);
//    }

    @Operation(summary = "Update a cinema hall")
    @PutMapping("/updateCinemaHall{id}")
    public CinemaHall updateCinemaHall(@RequestBody CinemaHall cinemaHall, @PathVariable("id") Long id)
    {
        return cinemaHallService.updateCinemaHall(cinemaHall, id);
    }

    @Operation(summary = "Delete a cinema hall")
    @DeleteMapping("/deleteCinemaHall{id}")
    public String deleteCinemaHallById(@PathVariable("id") Long id)
    {
        cinemaHallService.deleteById(id);
        return "Deleted Successfully";
    }


}
