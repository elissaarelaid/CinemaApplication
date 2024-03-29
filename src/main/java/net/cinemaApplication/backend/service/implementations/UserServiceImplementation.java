package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.entity.user.User;
import net.cinemaApplication.backend.repository.*;
import net.cinemaApplication.backend.service.services.SeatService;
import net.cinemaApplication.backend.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieSessionRepository movieSessionRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatService seatService;

    /**
     * Getting user's all tickets
     * @param userId represents user whose tickets to get
     * @return List of user's tickets
     */
    @Override
    public List<Ticket> getAllUserTickets(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user.get().getTickets();
    }

    /**
     * Get user's history according to the movies user has seen
     * @param userId represents user whose history to get
     * @return List of the movies user has seen
     */

    @Override
    public List<Movie> getHistory(Long userId) {
        List<Ticket> tickets = getAllUserTickets(userId);
        return tickets.stream()
                .map(ticket -> ticket.getSession().getMovie())
                .toList();
    }

    /**
     * Method recommends movies to the user according to the movies user has seen.
     * Gets genres user has seen the most and recommends movies with the same genre
     * @param id represents user to recommend movies
     * @return List of recommended movies
     */

    @Override
    public List<Movie> recommendMovies(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOpt.get();
        List<Movie> allWatchedMovies = getHistory(id);
        List<Genre> favouriteGenres = getFavoriteGenres(allWatchedMovies);

        //finds movies with the same genre as user's favorite genres
        return movieRepository.findAll().stream().filter(c -> favouriteGenres.contains(c.getGenre()) &&
                user.getTickets().stream().noneMatch(d -> d.getSession().getMovie() == c)).toList();
    }

    /**
     *
     * @param allWatchedMovies represents a list of movies that user has seen
     * @return list of favourite genres
     */
    private List<Genre> getFavoriteGenres(List<Movie> allWatchedMovies) {
        if (allWatchedMovies.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not seen any movies");
        }
        Map<Genre, Integer> genresAndCounts = new HashMap<>();  //genres and how many times the user has watched this genre
        for (Movie movie : allWatchedMovies) {
            if (genresAndCounts.containsKey(movie.getGenre())) {
                genresAndCounts.replace(movie.getGenre(), genresAndCounts.get(movie.getGenre()) + 1);
            } else {
                genresAndCounts.put(movie.getGenre(), 1);
            }
        }

        //sort a map to get most viewed genres, used https://www.liberiangeek.net/2024/01/sort-map-value-java/
        LinkedHashMap<Genre, Integer> sortedMap = genresAndCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry :: getKey, Map.Entry :: getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap :: new));

        List<Genre> allGenres = sortedMap.keySet().stream().toList();
        List<Genre> favouriteGenres = new ArrayList<>();
        int size = Math.min(allGenres.size(), 3); //if possible then get 3 favorite genres
        for (int i = 0; i < size; i++) {
            favouriteGenres.add(allGenres.get(i));
        }
        return favouriteGenres;
    }

    /**
     * Method for buying tickets to the movie session
     * @param sessionId represents a movie session to buy tickets to
     * @param userId represents a user who wants to buy a ticket
     * @param ticketAmount represents an amount of tickets user wants to buy
     * @return List of tickets user has bought
     */

    @Override
    public List<Ticket> buyMovieTickets(Long sessionId, Long userId, int ticketAmount) {
        Optional<MovieSession> sessionOptional = movieSessionRepository.findById(sessionId);
        if (sessionOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie session not found");
        }
        MovieSession session = sessionOptional.get();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOptional.get();
        List<Ticket> newTickets = new ArrayList<>();
        seatService.buyMovieTicketsToGenerateTakenSeats(session);

        assert session.getHall() != null;
        List<Seat> recommendedSeats = seatService.recommendSeats(ticketAmount, session);
        if (recommendedSeats == null || recommendedSeats.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are not that many free tickets left!");
        }

        for (int i = 0; i < ticketAmount; i++) {
            Seat seat = recommendedSeats.get(i);
            Ticket ticket = Ticket.builder()
                    .session(session)
                    .seat(seat)
                    .user(user)
                    .build();
            ticket.setPrice();
            Seat savedSeat = seatRepository.save(ticket.getSeat());
            ticket.setSeat(savedSeat);
            ticketRepository.save(ticket);
            session.getTickets().add(ticket);
            user.getTickets().add(ticket);
            newTickets.add(ticket);
        }

        session.setFreeSeats(session.getFreeSeats() - ticketAmount); //sets new amount of free seats to the session
        movieSessionRepository.save(session);
        userRepository.save(user);
        return newTickets;
    }


}
