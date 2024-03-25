package net.cinemaApplication.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.service.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class MovieController {
    @Autowired private MovieService movieService;

    @Operation(summary = "Get all movies")
    @GetMapping("/movies")
    public List<Movie> getAllMovies()
    {
        return movieService.getAllMovies();
    }

    @Operation(summary = "Get movie by id")
    @GetMapping("/movie{id}")
    public Movie getMovieById(@PathVariable("id") Long id)
    {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return movie.get();
    }

    @Operation(summary = "Get all movie sessions for a specific movie")
    @GetMapping("/movie{id}/sessions")
    public List<MovieSession> getAllSpecificMovieSessions(@PathVariable("id") Long id)
    {
        return movieService.getMovieSessionsForSpecificMovie(id);
    }

    @Operation(summary = "Get movie sessions for a specific movie by specific date")
    @GetMapping("/movieSession{id}/{date}")
    public List<MovieSession> getMovieSessionByIdAndDate
            (@PathVariable("id") Long id,
             @PathVariable("date") LocalDate date)
    {
        return movieService.getMovieSessionsForSpecificMovieAndDate(date, id);
    }

    @Operation(summary = "Get all movie sessions for a week")
    @GetMapping("/movieSessionsWeek/{id}/{date}")
    public List<MovieSession> getMovieSessionsForAWeek(@PathVariable("date") LocalDate date, @PathVariable("id") Long id)
    {
        return movieService.getMovieSessionsForSpecificMovieAndWeek(date, id);
    }

    @Operation(summary = "Add new movie")
    @PostMapping("/add_movie")
    public Movie addMovie(@Valid @RequestBody Movie movie)
    {
        return movieService.addMovie(movie);
    }

    @Operation(summary = "Update a movie")
    @PutMapping("/updateMovie{id}")
    public Movie updateMovie(@RequestBody Movie movie, @PathVariable("id") Long id)
    {
        return movieService.updateMovie(movie, id);
    }

    @Operation(summary = "Delete a movie")
    @DeleteMapping("/deleteMovie{id}")
    public String deleteMovieById(@PathVariable("id") Long id)
    {
        movieService.deleteById(id);
        return "Deleted Successfully";
    }

    @Operation(summary = "Add new movie session (path variable is id of the movie you want to add a session to)")
    @PostMapping("/add_movieSession{id}")
    public MovieSession addNewMovieSession(@PathVariable("id") Long id ,@Valid @RequestBody MovieSession movieSession) {
        return movieService.addNewMovieSessionToTheMovie(id, movieSession);
    }

}
