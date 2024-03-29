package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.entity.user.Ticket;

import java.util.List;

public interface UserService {
    List<Ticket> getAllUserTickets(Long userId);
    List<Movie> getHistory(Long userId);
    List<Movie> recommendMovies(Long userId);
    List<Ticket> buyMovieTickets(Long sessionId, Long userId, int ticketAmount);
}
