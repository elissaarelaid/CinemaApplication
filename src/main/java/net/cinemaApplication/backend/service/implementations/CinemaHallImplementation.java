package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movie.Movie;
import net.cinemaApplication.backend.repository.CinemaHallRepository;
import net.cinemaApplication.backend.repository.MovieSessionRepository;
import net.cinemaApplication.backend.repository.SeatRepository;
import net.cinemaApplication.backend.service.services.CinemaHallService;
import net.cinemaApplication.backend.service.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CinemaHallImplementation implements CinemaHallService {
    @Autowired
    CinemaHallRepository cinemaHallRepository;

    @Autowired
    MovieSessionRepository movieSessionRepository;
    @Autowired
    SeatRepository seatRepository;

    /**
     * @return List of all the cinema halls created
     */
    @Override
    public List<CinemaHall> getAllCinemaHalls() {
        return cinemaHallRepository.findAll();
    }

    /**
     *
     * @param id of the cinema hall
     * @return an optional of the cinema hall
     */
    @Override
    public Optional<CinemaHall> getCinemaHallById(Long id) {
        return cinemaHallRepository.findById(id);
    }


    /**
     * Method for adding new cinema hall to the system
     * While creating a new hall, the system automatically adds seats according to cinema hall rows and columns
     * @param cinemaHall that client wants to create
     * @return created cinema hall
     */
    @Override
    public CinemaHall addCinemaHall(CinemaHall cinemaHall) {
        if(cinemaHall.getHallNr() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hall number must be positive");
        }
        if (cinemaHallRepository.findByHallNr(cinemaHall.getHallNr()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hall nr is already taken");
        }
        if (cinemaHall.getSeatColumns() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat column number must be positive");
        }
        if (cinemaHall.getSeatRows() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat row number must be positive");
        }
        CinemaHall savedCinemaHall = cinemaHallRepository.save(cinemaHall);

        addSeatsToTheMovieHall(savedCinemaHall); //adds seats to the cinema hall
        return savedCinemaHall;
    }


    /**
     * deletes cinema hall from the system (also deletes movie sessions and tickets related to this)
     * @param id represents cinema hall id
     */
    @Override
    public void deleteById(Long id) {
        if (cinemaHallRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found");
        }
        cinemaHallRepository.deleteById(id);
    }

    /**
     * Method for adding new seats to the cinema hall. Method is used while creating a new hall
     * @param cinemaHall to add seats to
     */
    private void addSeatsToTheMovieHall(CinemaHall cinemaHall) {
        if (cinemaHallRepository.findById(cinemaHall.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found");
        }

        int totalSeatsAllowed = cinemaHall.getSeatColumns() * cinemaHall.getSeatRows();
        int existingSeats = cinemaHall.getSeats() != null ? cinemaHall.getSeats().size() : 0;

        if (existingSeats >= totalSeatsAllowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are already enough seats");
        }

        int seatsToAdd = totalSeatsAllowed - existingSeats;
        for (int i = 0; i < seatsToAdd; i++) {
            Seat seat = Seat.builder()
                    .seatNr(existingSeats + i + 1)
                    .hall(cinemaHall)
                    .build();
            cinemaHall.getSeats().add(seat);
            seatRepository.save(seat);
        }
        cinemaHallRepository.save(cinemaHall);
    }

}
