package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.Genre;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.entity.user.User;
import net.cinemaApplication.backend.repository.*;
import net.cinemaApplication.backend.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    @Override
    public List<Ticket> getAllTickets(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user.get().getTickets();
    }

    @Override
    public List<Movie> getHistory(Long userId) {
        List<Ticket> tickets = getAllTickets(userId);
        return tickets.stream()
                .map(ticket -> ticket.getSession().getMovie())
                .toList();
    }

    @Override
    public List<Movie> recommendMovies(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        List<Movie> allWatchedMovies = getHistory(id);
        Map<Genre, Integer> genresAndCounts = new HashMap<>();  //genres and how many times the user has watched this genre
        for (Movie movie : allWatchedMovies) {
            if (genresAndCounts.containsKey(movie.getGenre())) {
                genresAndCounts.replace(movie.getGenre(), genresAndCounts.get(movie.getGenre()) + 1);
            } else {
                genresAndCounts.put(movie.getGenre(), 1);
            }
        }
        //sort a map, used https://www.liberiangeek.net/2024/01/sort-map-value-java/
        LinkedHashMap<Genre, Integer> sortedMap = genresAndCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry :: getKey, Map.Entry :: getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap :: new));

        List<Genre> allGenres = sortedMap.keySet().stream().toList(); //ToDo: ei arvesta sellega kui sama zanri sama palju vaadatud
        List<Genre> favouriteGenres = new ArrayList<>();
        int size = Math.min(allGenres.size(), 3); //if possible then recommends 3 genres
        for (int i = 0; i < size; i++) {
            favouriteGenres.add(allGenres.get(i));
        }
        return movieRepository.findAll().stream().filter(c -> favouriteGenres.contains(c.getGenre())).toList();
    }

    @Override
    public List<Ticket> buyMovieTickets(Long sessionId, Long userId, int ticketAmount) { //has to recommend seats here!!
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
        generateTakenSeats(session);
        Random rnd = new Random();

        for (int i = 0; i < ticketAmount; i++) {
            List<Seat> availableSeats = session.getHall().getSeats().stream().filter(c -> !c.isSeatTaken()).toList();
            Seat seat = availableSeats.get(rnd.nextInt(availableSeats.size() - 1));
            Ticket ticket = Ticket.builder()
                    .session(session)
                    .seat(seat)
                    .user(user)
                    .status(true)
                    .build();
            ticket.setPrice();
            seat.setSeatTaken(true);
            Seat savedSeat = seatRepository.save(ticket.getSeat());
            ticket.setSeat(savedSeat);
            ticketRepository.save(ticket);
            session.getTickets().add(ticket);
            user.getTickets().add(ticket);
            newTickets.add(ticket);
        }

        movieSessionRepository.save(session);
        userRepository.save(user);
        return newTickets;
    }

    private void generateTakenSeats(MovieSession session) {
        CinemaHall hall = session.getHall();
        List<Seat> seats = hall.getSeats();
        Random rnd = new Random();
        for (Seat seat : seats) {
            if (!seat.isSeatTaken()) {
                seat.setSeatTaken(rnd.nextBoolean());
            }
            seatRepository.save(seat);
        }
    }
    @Override
    public Ticket cancelTicket(Long ticketId, Long userId) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Ticket ticket = ticketOptional.get();
        User user = userOptional.get();
        if (!user.getTickets().contains(ticket)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have this ticket");
        }
        ticket.setStatus(false); //cancelled ticket
        ticketRepository.save(ticket);
        return ticket;
    }
}
