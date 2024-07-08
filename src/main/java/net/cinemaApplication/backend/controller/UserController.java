package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "Operations related to users in the cinema system")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all user's tickets",
            description = "Retrieves a list of all tickets purchased by a specific user, identified by their id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned list of all tickets purchased by the user."),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/user/{id}/tickets")
    public List<Ticket> getAllUserTickets(@PathVariable("id") Long id) {
        return userService.getAllUserTickets(id);
    }


    @Operation(summary = "Get all user's history",
            description = "Fetches a history of all movies watched by a specific user, identified by their id. " +
                    "The history is based on the tickets purchased",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned list of movies watched by the user."),
                    @ApiResponse(responseCode = "404", description = "User not found or user has not watched any movies")
            })
    @GetMapping("/user/{id}/history")
    public List<Movie> getUserHistory(@PathVariable("id") Long id) {
        return userService.getHistory(id);
    }

    @Operation(summary = "Recommend movies to the user according to their history",
            description = "Provides movie recommendations for a specific user based on their movie watching history." +
                    "The recommendations focus on genres the user has shown interest in.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned list of recommended movies."),
                    @ApiResponse(responseCode = "404", description = "User not found or " +
                            "there are no recommendations available based on the user's history.")
            })
    @GetMapping("/user/{id}/recommendation")
    public List<Movie> getUserRecommendations(@PathVariable("id") Long id) {
        return userService.recommendMovies(id);
    }


    @Operation(summary = "Buy tickets",
            description = "Allows a user to purchase a specified number of tickets for a movie session. " +
                    "The user and session are identified by their ids",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tickets successfully purchased"),
                    @ApiResponse(responseCode = "400", description = "Invalid request. " +
                            "Possible reasons could be no available seats, past session date, etc"),
                    @ApiResponse(responseCode = "404", description = "User or movie session not found")
            })
    @GetMapping("/user{userId}/buy{sessionId}/{ticketAmount}")
    public List<Ticket> buyTickets(@PathVariable("userId") Long userId, @PathVariable("sessionId") Long sessionId,
                                   @PathVariable("ticketAmount") int ticketAmount) {
        return userService.buyMovieTickets(sessionId, userId, ticketAmount);
    }

}
