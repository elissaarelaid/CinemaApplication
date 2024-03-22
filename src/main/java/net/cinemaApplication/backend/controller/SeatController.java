package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.service.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class SeatController {
    @Autowired
    private SeatService seatService;

    @Operation(summary = "Get all seats")
    @GetMapping("/seats")
    public List<Seat> getAllSeats()
    {
        return seatService.getAllSeats();
    }

    @Operation(summary = "Get seat by id")
    @GetMapping("/seat{id}")
    public Seat getSeatById(@PathVariable("id") Long id)
    {
        Optional<Seat> seat = seatService.getAllSeats().stream().filter(c -> Objects.equals(c.getId(), id)).findFirst();
        if (seat.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return seat.get();
    }

    @Operation(summary = "Save a seat object")
    @PostMapping("/save_seat")
    public Seat saveSeat(@Valid @RequestBody Seat seat)
    {
        return seatService.saveSeat(seat);
    }

    @Operation(summary = "Update a seat")
    @PutMapping("/updateSeat{id}")
    public Seat updateSeat(@RequestBody Seat seat, @PathVariable("id") Long id)
    {
        return seatService.updateSeat(seat, id);
    }

    @Operation(summary = "Delete a seat")
    @DeleteMapping("/deleteSeat{id}")
    public String deleteSeatById(@PathVariable("id") Long id)
    {
        seatService.deleteById(id);
        return "Deleted Successfully";
    }
}
