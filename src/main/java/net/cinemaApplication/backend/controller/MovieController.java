package net.cinemaApplication.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movie", description = "Operations related to movies in the cinema system")
public class MovieController {
    @Autowired private MovieService movieService;

    @Operation(summary = "Get all movies" ,
            description = "Retrieves a list of all the movies",
            responses = {@ApiResponse(responseCode = "200", description = "All movies successfully returned")})
    @GetMapping("/movies")
    public List<Movie> getAllMovies()
    {
        return movieService.getAllMovies();
    }

    @Operation(summary = "Get movie by id",
            description = "Retrieves a movie by id",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully returned a movie by id"),
            @ApiResponse(responseCode = "404", description = "Movie not found")})
    @GetMapping("/movie{id}")
    public Movie getMovieById(@PathVariable("id") Long id)
    {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        return movie.get();
    }

    @Operation(summary = "Add new movie",
            description = "Adds new movie." +
                    " Ensures title, description and movie director name lengths are correct. Checks if age limit and genre are correct " +
                    "and movie rating is between correct values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "New movie created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input: title, description or movie director " +
                            "name is not with the correct length, " +
                            "Age limit or genre is not correct, " +
                            "Rating is not between correct values")})
    @PostMapping("/add_movie")
    public Movie addMovie(@Valid @RequestBody Movie movie)
    {
        return movieService.addMovie(movie);
    }


    @Operation(summary = "Update a movie",
            description = "Updates a movie and also end time of the movie sessions that are related to this movie" +
                    " Ensures title, description and movie director name lengths are correct. Checks if age limit and genre are correct " +
                    "and movie rating is between correct values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input: title, description or movie director " +
                            "name is not with the correct length, " +
                            "Age limit or genre is not correct, " +
                            "Rating is not between correct values"),
            @ApiResponse(responseCode = "404", description = "Movie not found")})
    @PutMapping("/updateMovie{id}")
    public Movie updateMovie(@RequestBody Movie movie, @PathVariable("id") Long id)
    {
        return movieService.updateMovie(movie, id);
    }

    @Operation(summary = "Delete a movie",
            description = "Deletes a movie by id",
            responses = {@ApiResponse(responseCode = "200", description = "Movie is successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Movie not found")})
    @DeleteMapping("/deleteMovie{id}")
    public String deleteMovieById(@PathVariable("id") Long id)
    {
        movieService.deleteById(id);
        return "Deleted Successfully";
    }

}
