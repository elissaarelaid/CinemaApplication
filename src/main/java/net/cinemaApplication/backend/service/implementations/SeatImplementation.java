package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.repository.SeatRepository;
import net.cinemaApplication.backend.service.services.CinemaHallService;
import net.cinemaApplication.backend.service.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SeatImplementation implements SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private CinemaHallService cinemaHallService;

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
}
