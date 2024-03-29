package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.repository.SeatRepository;
import net.cinemaApplication.backend.repository.TicketRepository;
import net.cinemaApplication.backend.service.services.CinemaHallService;
import net.cinemaApplication.backend.service.services.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatImplementation implements SeatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImplementation.class);
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private CinemaHallService cinemaHallService;

    @Autowired
    private TicketRepository ticketRepository;

    /**
     *
     * @return a list of all the seats in the system
     */
    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    /**
     *
     * @return a seat by id
     */
    @Override
    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }


    /**
     * Delete a seat by id, also deletes tickets related to this seat
     * @param id of the seat
     * @throws ResponseStatusException if seat is missing
     */
    @Override
    public void deleteById(Long id) {
        if (seatRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found");
        }
        seatRepository.deleteById(id);
    }

    /**
     * Get all the seats from a specific cinema hall
     * @param hallId represents a cinema hall where the seats are
     * @return list of seats in this cinema hall
     * @throws ResponseStatusException if cinema hall is missing
     */
    @Override
    public List<Seat> getAllSeatsFromCinemaHall(Long hallId) {
        Optional<CinemaHall> cinemaHallOptional = cinemaHallService.getCinemaHallById(hallId);
        if (cinemaHallOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found");
        }
        CinemaHall cinemaHall = cinemaHallOptional.get();
        return getAllSeats().stream().filter(c -> c.getHall() == cinemaHall).toList();
    }

    /**
     * Method for automatically generating taken seats. Method buys random number of tickets to this movie session
     * Method runs when user tries to buy tickets
     * @param session to generate tickets for
     */
    @Override
    public void buyMovieTicketsToGenerateTakenSeats(MovieSession session) {
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

    /**
     * Recommends seats to the user
     * @param amount of seats to recommend (same as the ticket amount)
     * @param session to which user wants to buy a ticket to
     * @return list of the best available seats
     */
    //used chatgpt help
    @Override
    public List<Seat> recommendSeats(int amount, MovieSession session) {
        List<Seat> availableSeats = getSortedAvailableSeats(session);

        //if there's no available seats then returns null
        if (availableSeats.size() < amount) {
            return null;
        }

        CinemaHall hall = session.getHall();

        //Tries to find the best continuous seats. If it finds as many continuous seats as asked then returns a list of them
        List<Seat> bestContinuousSeats = findBestContinuousSeats(amount, availableSeats, hall);
        if (!bestContinuousSeats.isEmpty()) {
            return bestContinuousSeats;
        } else { //if it cannot find as many continuous seats as asked then finds as many continuous seats as possible.
            //For the rest of the seats finds the best ones.
            for (int i = amount; i > 0; i--) {
                List<Seat> continuousSeats = findBestContinuousSeats(i, availableSeats, hall);
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

    /**
     * Sorts all the free seats by their distance from the center
     * @param session
     * @return
     */
    private List<Seat> getSortedAvailableSeats(MovieSession session) {
        CinemaHall hall = session.getHall();
        List<Seat> allSeats = hall.getSeats();
        int hallRows = hall.getSeatRows();
        int hallColumns = hall.getSeatColumns();
        int middleRow = hallRows / 2;
        int middleColumn = hallColumns / 2;
        return allSeats.stream()
                .filter(seat -> seat.getTickets().stream().noneMatch(c -> c.getSession() == session))
                .sorted(Comparator.comparingInt(seat -> Math.abs(getSeatRow(seat, hall) - middleRow)
                        + Math.abs(getSeatColumn(seat, hall) - middleColumn)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //used chatgpt help

    /**
     * Finds best continuous seats
     * Continuous seats are defined as seats in the same row without any gaps between them.
     * @param amount of the seats asked
     * @param availableSeats for this session
     * @param hall where the session takes place
     * @return the list of the best continuous seats
     */
    private List<Seat> findBestContinuousSeats(int amount, List<Seat> availableSeats, CinemaHall hall) {
        int hallRows = hall.getSeatRows();
        int hallColumns = hall.getSeatColumns();

        List<Seat> continuousSeats = new ArrayList<>();
        if (amount == 1) {
            //If only one seat is requested, return the first available seat
            continuousSeats.add(availableSeats.get(0));
            return continuousSeats;
        }
        for (int row = 1; row <= hallRows; row++) {
            for (int startColumn = 1; startColumn <= hallColumns; startColumn++) {
                int endColumn = startColumn + amount - 1;
                if (endColumn > hallColumns) break; //if row ends then take new row

                //check if all seats are free in this range of seats
                int finalRow = row;
                int finalStartColumn = startColumn;
                //filter available seats to find a continuous range in the specified row and column range
                continuousSeats = availableSeats.stream()
                        .filter(seat -> getSeatRow(seat, hall) == finalRow
                                && getSeatColumn(seat, hall) >= finalStartColumn
                                && getSeatColumn(seat, hall) <= endColumn)
                        .collect(Collectors.toList());

                if (continuousSeats.size() == amount) {
                    // if found the required amount of continuous seats, return them
                    return continuousSeats;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gets the row where the seat is in the cinema hall
     * @param seat to find the row of
     * @param hall where the seat is located
     * @return row of the seat
     */
    private int getSeatRow(Seat seat, CinemaHall hall) {
        return (seat.getSeatNr() - 1) / hall.getSeatColumns() + 1;
    }

    /**
     * Gets the column where the seat is in the cinema hall
     * @param seat to find the column of
     * @param hall where the seat is located
     * @return column of the seat
     */

    private int getSeatColumn(Seat seat, CinemaHall hall) {
        return (seat.getSeatNr() - 1) % hall.getSeatColumns() + 1;
    }
}
