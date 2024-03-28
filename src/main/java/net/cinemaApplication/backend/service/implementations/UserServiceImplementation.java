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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImplementation implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImplementation.class);
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
        User user = userOpt.get();
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

        return movieRepository.findAll().stream().filter(c -> favouriteGenres.contains(c.getGenre()) &&
                user.getTickets().stream().noneMatch(d -> d.getSession().getMovie() == c)).toList();
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
        buyMovieTicketsToGenerateTakenSeats(session);

        assert session.getHall() != null;
        List<Seat> recommendedSeats = recommendSeats(ticketAmount, session);
        if (recommendedSeats == null) {
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

        session.setFreeSeats(session.getFreeSeats() - ticketAmount);
        movieSessionRepository.save(session);
        userRepository.save(user);
        return newTickets;
    }

    private void buyMovieTicketsToGenerateTakenSeats(MovieSession session) {
        CinemaHall hall = session.getHall();
        List<Seat> seats = hall.getSeats();
        Random rnd = new Random();
        int takenSeatsCounter = 0;
        for (Seat seat : seats) {
            if (seat.getTickets().stream().noneMatch(c -> c.getSession() == session)) {
                boolean randomBoolean = rnd.nextBoolean();
                if (randomBoolean) {
                    Ticket ticket = Ticket.builder()
                            .session(session)
                            .seat(seat)
                            .build();
                    ticket.setPrice();
                    Seat savedSeat = seatRepository.save(ticket.getSeat());
                    ticket.setSeat(savedSeat);
                    ticketRepository.save(ticket);
                    session.getTickets().add(ticket);
                    LOGGER.info("Ticket created for seat: {}", seat.getSeatNr());
                    takenSeatsCounter++;
                }
                seatRepository.save(seat);
            } else {
                takenSeatsCounter++;
            }

        }
        int newFreeSeats = session.getFreeSeats() - takenSeatsCounter;
        session.setFreeSeats(newFreeSeats);
        LOGGER.info("New free seats: {}", newFreeSeats);
    }


    //used chatgpt help
    private List<Seat> recommendSeats(int amount, MovieSession session) {
        CinemaHall hall = session.getHall();
        List<Seat> allSeats = hall.getSeats();
        int hallRows = hall.getSeatRows();
        int hallColumns = hall.getSeatColumns();
        int middleRow = hallRows / 2;
        int middleColumn = hallColumns / 2;

        //sort all the free seats by their distance from the center
        assert allSeats != null;

        List<Seat> availableSeats = allSeats.stream()
                .filter(seat -> seat.getTickets().stream().noneMatch(c -> c.getSession() == session))
                .sorted(Comparator.comparingInt(seat -> Math.abs(getSeatRow(seat, hall) - middleRow)
                        + Math.abs(getSeatColumn(seat, hall) - middleColumn)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (availableSeats.size() < amount) {
            return null;
        }

        List<Seat> bestContinuousSeats = findBestContinuousSeats(amount, hallRows, hallColumns, availableSeats, hall);
        if (!bestContinuousSeats.isEmpty()) {
            return bestContinuousSeats;
        } else {
            for (int i = amount; i > 0; i--) {
                List<Seat> continuousSeats = findBestContinuousSeats(i, hallRows, hallColumns, availableSeats, hall);
                if (!continuousSeats.isEmpty()) {
                    availableSeats.removeAll(continuousSeats);
                    if (i == amount) {
                        return continuousSeats;
                    } else {
                        continuousSeats.addAll(availableSeats.subList(0, amount - i));
                        return continuousSeats;
                    }
                }
            }

        }
        return new ArrayList<>();
    }
    //used chatgpt help

    private List<Seat> findBestContinuousSeats(int amount, int hallRows, int hallColumns, List<Seat> availableSeats, CinemaHall hall) {
        List<Seat> continuousSeats = new ArrayList<>();
        if (amount == 1) {
            continuousSeats.add(availableSeats.get(0));
            return continuousSeats;
        }
        for (int row = 1; row <= hallRows; row++) {
            for (int startColumn = 1; startColumn <= hallColumns; startColumn++) {
                int endColumn = startColumn + amount - 1;
                if (endColumn > hallColumns) break; //if row ends then take new row

                //check if all seats are free
                int finalRow = row;
                int finalStartColumn = startColumn;
                continuousSeats = availableSeats.stream()
                        .filter(seat -> getSeatRow(seat, hall) == finalRow
                                && getSeatColumn(seat, hall) >= finalStartColumn
                                && getSeatColumn(seat, hall) <= endColumn)
                        .collect(Collectors.toList());


                if (continuousSeats.size() == amount) {
                    return continuousSeats;
                }
            }
        }
        return new ArrayList<>();
    }

    private int getSeatRow(Seat seat, CinemaHall hall) {
       return (seat.getSeatNr() - 1) / hall.getSeatColumns() + 1;
    }

    private int getSeatColumn(Seat seat, CinemaHall hall) {
        return (seat.getSeatNr() - 1) % hall.getSeatColumns() + 1;
    }

//
//    @Override
//    public Ticket cancelTicket(Long ticketId, Long userId) { //todo:vabastada koht saalis!!!
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//        if (ticketOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
//        }
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }
//        Ticket ticket = ticketOptional.get();
//        User user = userOptional.get();
//        if (!user.getTickets().contains(ticket)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have this ticket");
//        }
//        ticket.setStatus(false); //cancelled ticket
//        ticketRepository.save(ticket);
//        return ticket;
//    }
}
