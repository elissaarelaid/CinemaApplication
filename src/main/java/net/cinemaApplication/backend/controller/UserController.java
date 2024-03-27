package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Get all user's tickets")
    @GetMapping("/user{id}/tickets")
    public List<Ticket> getAllUserTickets(@PathVariable("id") Long id)
    {
        return userService.getAllTickets(id);
    }

    @Operation(summary = "Get all user's history")
    @GetMapping("/user{id}/history")
    public List<Movie> getUserHistory(@PathVariable("id") Long id)
    {
        return userService.getHistory(id);
    }

//    @Operation(summary = "User wants to cancel a ticket")
//    @GetMapping("/user{userId}/ticket{ticketId}/cancel")
//    public Ticket cancelATicket(@PathVariable("userId") Long userId, @PathVariable("ticketId") Long ticketId)
//    {
//        return userService.cancelTicket(ticketId, userId);
//    }

    @Operation(summary = "Recommend movies to the user according to their history")
    @GetMapping("/user{id}/recommend")
    public List<Movie> getUserRecommendations(@PathVariable("id") Long id)
    {
        return userService.recommendMovies(id);
    }

    @Operation(summary = "Buy tickets")
    @GetMapping("/user{userId}/buy{sessionId}/{ticketAmount}")
    public List<Ticket> buyTickets(@PathVariable("userId") Long userId, @PathVariable("sessionId") Long sessionId,
                             @PathVariable("ticketAmount") int ticketAmount)
    {
        return userService.buyMovieTickets(sessionId, userId, ticketAmount);
    }
}
