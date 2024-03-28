package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.service.services.MovieService;
import net.cinemaApplication.backend.service.services.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class MovieSessionController {
    @Autowired
    private MovieSessionService movieSessionService;


    @Operation(summary = "Get all movies sessions")
    @GetMapping("/movieSessions")
    public List<MovieSession> getAllMovieSessions()
    {
        return movieSessionService.getAllMovieSessions();
    }

    @Operation(summary = "Get movie session by id")
    @GetMapping("/movieSession{id}")
    public MovieSession getMovieSessionById(@PathVariable("id") Long id)
    {
        Optional<MovieSession> movieSession = movieSessionService.getMovieSessionById(id);
        if (movieSession.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return movieSession.get();
    }

    @Operation(summary = "Get all movie sessions by specific date")
    @GetMapping("/movieSession/{date}")
    public List<MovieSession> getMovieSessionsByDate(@PathVariable("date") LocalDate date)
    {
        return movieSessionService.getAllMovieSessionsForSpecificDate(date);
    }

    @Operation(summary = "Get all movie sessions for a week")
    @GetMapping("/movieSessionWeek/{date}")
    public List<MovieSession> getMovieSessionsForAWeek(@PathVariable("date") LocalDate date)
    {
        return movieSessionService.getAllMovieSessionsForAWeek(date);
    }


    @Operation(summary = "Update a movie session")
    @PutMapping("/updateMovieSession{id}")
    public MovieSession updateMovieSession(@RequestBody MovieSession movieSession, @PathVariable("id") Long id)
    {
        return movieSessionService.updateMovieSession(movieSession, id);
    }

    @Operation(summary = "Delete a movie session")
    @DeleteMapping("/deleteMovieSession{id}")
    public String deleteMovieSessionById(@PathVariable("id") Long id)
    {
        movieSessionService.deleteById(id);
        return "Deleted Successfully";
    }
    @Operation(summary = "Get all movie sessions for a specific movie")
    @GetMapping("/movie{id}/sessions")
    public List<MovieSession> getAllSpecificMovieSessions(@PathVariable("id") Long id)
    {
        return movieSessionService.getMovieSessionsForSpecificMovie(id);
    }

    @Operation(summary = "Get movie sessions for a specific movie by specific date")
    @GetMapping("/movieSession{id}/{date}")
    public List<MovieSession> getMovieSessionByIdAndDate
            (@PathVariable("id") Long id,
             @PathVariable("date") LocalDate date)
    {
        return movieSessionService.getMovieSessionsForSpecificMovieAndDate(date, id);
    }

    @Operation(summary = "Get all movie sessions for a week")
    @GetMapping("/movieSessionsWeek/{id}/{date}")
    public List<MovieSession> getMovieSessionsForAWeek(@PathVariable("date") LocalDate date, @PathVariable("id") Long id)
    {
        return movieSessionService.getMovieSessionsForSpecificMovieAndWeek(date, id);
    }

    @Operation(summary = "Add new movie session (path variable is id of the movie you want to add a session to)")
    @PostMapping("/add/movieSession{movieId}/{hallId}")
    public MovieSession addNewMovieSession(@PathVariable("movieId") Long movieId, @Valid @RequestBody MovieSession movieSession, @PathVariable("hallId") Long hallId ) {
        return movieSessionService.addNewMovieSessionToTheMovie(movieId, movieSession, hallId);
    }

}
