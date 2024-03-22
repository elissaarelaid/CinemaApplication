package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
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

    @Operation(summary = "Save cinema hall object")
    @PostMapping("/save_cinemaHall")
    public CinemaHall saveCinemaHall(@Valid @RequestBody CinemaHall cinemaHall)
    {
        return cinemaHallService.saveCinemaHall(cinemaHall);
    }

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
