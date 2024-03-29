package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.cinemaApplication.backend.entity.movie.AgeLimit;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movieSession.Language;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.service.services.MovieService;
import net.cinemaApplication.backend.service.services.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Movie session", description = "Operations related to movie sessions in the cinema systme")
public class MovieSessionController {
    @Autowired
    private MovieSessionService movieSessionService;


    @Operation(summary = "Get all movies sessions",
    description = "Retrieves all the movie sessions",
    responses = {@ApiResponse(responseCode = "200", description = "All cinema halls successfully created")})
    @GetMapping("/movieSessions")
    public List<MovieSession> getAllMovieSessions()
    {
        return movieSessionService.getAllMovieSessions();
    }

    @Operation(summary = "Get movie session by id",
            description = "Retrieves movie session by id",
            responses = {@ApiResponse(responseCode = "200", description = "Movie session successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Movie session not found")})
    @GetMapping("/movieSession/{id}")
    public MovieSession getMovieSessionById(@PathVariable("id") Long id)
    {
        Optional<MovieSession> movieSession = movieSessionService.getMovieSessionById(id);
        if (movieSession.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return movieSession.get();
    }

    @Operation(summary = "Get all movie sessions for a specific date, date input format: 2024-03-22",
    description = "Retrieves movie sessions for a specific date",
            responses = {@ApiResponse(responseCode = "200", description = "Movie sessions successfully returned")})
    @GetMapping("/movieSession/{date}")
    public List<MovieSession> getMovieSessionsByDate(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        return movieSessionService.getAllMovieSessionsForSpecificDate(date);
    }

    @Operation(summary = "Get all movie sessions for a week, date input format: 2024-03-22",
            description = "Retrieves all movie sessions for one week",
            responses = {@ApiResponse(responseCode = "200", description = "Movie sessions successfully returned")})
    @GetMapping("/movieSessionWeek/{date}")
    public List<MovieSession> getMovieSessionsForAWeek(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        return movieSessionService.getAllMovieSessionsForAWeek(date);
    }


    @Operation(summary = "Update a movie session",
            description = "Updates a movie session",
            responses = {@ApiResponse(responseCode = "200", description = "Movie sessions successfully updated"),
            @ApiResponse(responseCode = "404", description = "Movie session not found"),
            @ApiResponse(responseCode = "400", description = "Session date is past, " +
                    "movie format or language is not correct")})
    @PutMapping("/updateMovieSession/{id}")
    public MovieSession updateMovieSession(@RequestBody MovieSession movieSession, @PathVariable("id") Long id)
    {
        return movieSessionService.updateMovieSession(movieSession, id);
    }

    @Operation(summary = "Delete a movie session",
    description = "Deletes a movie session",
    responses = {@ApiResponse(responseCode = "200", description = "Movie session successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Movie session not found")})
    @DeleteMapping("/deleteMovieSession/{id}")
    public String deleteMovieSessionById(@PathVariable("id") Long id)
    {
        movieSessionService.deleteById(id);
        return "Deleted Successfully";
    }

    @Operation(summary = "Get all movie sessions for a specific movie",
    description = "Retrieves all movie sessions for a specific movie",
            responses = {@ApiResponse(responseCode = "200", description = "Movie sessions successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Movie not found")})
    @GetMapping("/movie/{id}/sessions")
    public List<MovieSession> getAllSpecificMovieSessions(@PathVariable("id") Long id)
    {
        return movieSessionService.getMovieSessionsForSpecificMovie(id);
    }

    @Operation(summary = "Get movie sessions for a specific movie by specific date",
            description = "Retrieves all movie sessions associated with a specific movie ID and happening on a specific date.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie sessions successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Movie not found")
            })
    @GetMapping("/movieSession{id}/{date}")
    public List<MovieSession> getMovieSessionsByMovieIdAndDate(@PathVariable("id") Long id,
                                                               @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieSessionService.getMovieSessionsForSpecificMovieAndDate(date, id);
    }


    @Operation(summary = "Get all movie sessions for a week from a specific date, date input format: 2024-03-22",
            description = "Retrieves all movie sessions for a specific movie happening within a week from the specified date.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie sessions for the week successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Movie not found")
            })
    @GetMapping("/movieSessionsWeek/{id}/{date}")
    public List<MovieSession> getMovieSessionsForAWeek(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                       @PathVariable("id") Long id) {
        return movieSessionService.getMovieSessionsForSpecificMovieAndWeek(date, id);
    }


    @Operation(summary = "Add new movie session to a specific movie",
            description = "Creates and adds a new movie session for a specific movie. Requires movie ID, session details, and cinema hall ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "New movie session successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or movie/hall not found"),
                    @ApiResponse(responseCode = "404", description = "Movie or cinema hall not found")
            })
    @PostMapping("/add/movieSession{movieId}/{hallId}")
    public MovieSession addNewMovieSession(@PathVariable("movieId") Long movieId,
                                           @Valid @RequestBody MovieSession movieSession,
                                           @PathVariable("hallId") Long hallId) {
        return movieSessionService.addNewMovieSessionToTheMovie(movieId, movieSession, hallId);
    }
    @Operation(summary = "Get movie sessions by language",
            description = "Retrieves all movie sessions in a specific language on a specific date. " +
                    "The date format should be YYYY-MM-DD, and the language should be one of the defined enums (e.g., ENGLISH).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie sessions successfully returned based on language preference"),
                    @ApiResponse(responseCode = "400", description = "Invalid language specified")
            })
    @GetMapping("get/moviesessionsLanguage/{language}/{date}")
    public List<MovieSession> getMovieSessionsByLanguage(@PathVariable("language") Language language, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieSessionService.filterByLanguage(language, date);
    }

    @Operation(summary = "Get movie sessions by genre",
            description = "Retrieves all movie sessions of a specific genre on a specific date. " +
                    "Ensure the date is in the format YYYY-MM-DD and the genre matches one of the available genres.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie sessions successfully returned based on genre preference"),
                    @ApiResponse(responseCode = "400", description = "Invalid genre specified")
            })
    @GetMapping("get/moviesessionsGenre/{genre}/{date}")
    public List<MovieSession> getMovieSessionsByGenre(@PathVariable("genre") Genre genre, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieSessionService.filterByGenre(genre, date);
    }

    @Operation(summary = "Get movie sessions by age limit",
            description = "Fetches all movie sessions that adhere to a specific age limit on a given date. " +
                    "Date should be formatted as YYYY-MM-DD. The age limit should be one of the predefined values.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie sessions successfully returned based on age limit"),
                    @ApiResponse(responseCode = "400", description = "Invalid age limit specified")
            })
    @GetMapping("get/moviesessionsAgeLimit/{ageLimit}/{date}")
    public List<MovieSession> getMovieSessionsByAgeLimit(@PathVariable("ageLimit") AgeLimit ageLimit, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieSessionService.filterByAgeLimit(ageLimit, date);
    }

    @Operation(summary = "Get movie sessions by start time",
            description = "Retrieves movie sessions that start after a specific time on a given date. " +
                    "Use the format YYYY-MM-DD for the date and HH:MM for start time.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie sessions successfully returned based on start time"),
                    @ApiResponse(responseCode = "400", description = "Invalid start time specified")
            })
    @GetMapping("get/moviesessionsStartTime/{startTime}/{date}")
    public List<MovieSession> getMovieSessionsByStartTime(@PathVariable("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieSessionService.filterByStartTime(startTime, date);
    }


}
