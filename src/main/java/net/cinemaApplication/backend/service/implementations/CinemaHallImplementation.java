package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import net.cinemaApplication.backend.repository.CinemaHallRepository;
import net.cinemaApplication.backend.repository.MovieSessionRepository;
import net.cinemaApplication.backend.repository.SeatRepository;
import net.cinemaApplication.backend.service.services.CinemaHallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
@Service
public class CinemaHallImplementation implements CinemaHallService {
    @Autowired
    CinemaHallRepository cinemaHallRepository;

    @Autowired
    MovieSessionRepository movieSessionRepository;
    @Autowired
    SeatRepository seatRepository;
    @Override
    public CinemaHall addCinemaHall(CinemaHall cinemaHall) {
        if (cinemaHallRepository.findAll().stream().anyMatch(c -> c.getHallNr() == cinemaHall.getHallNr())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hall nr is already taken");
        }
        if (cinemaHall.getSeatColumns() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat column number must be positive");
        }
        if (cinemaHall.getSeatRows() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat row number must be positive");
        }
        CinemaHall savedCinemaHall = cinemaHallRepository.save(cinemaHall);

        addSeatsToTheMovieHall(savedCinemaHall);
        return savedCinemaHall;
    }


    @Override
    public List<CinemaHall> getAllCinemaHalls() {
        return cinemaHallRepository.findAll();
    }

    @Override
    public CinemaHall updateCinemaHall(CinemaHall cinemaHall, Long id) {
        CinemaHall cinemaHallFromDb = cinemaHallRepository.findById(id).get(); //get old cinema hall from database

        //hall nr has to be unique
        if (cinemaHall.getHallNr() != cinemaHallFromDb.getHallNr()) {
            if (cinemaHallRepository.findAll().stream().anyMatch(c -> c.getHallNr() == cinemaHall.getHallNr())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hall nr is already taken");
            }
        }
        if (cinemaHall.getSeatColumns() > 0) {
            cinemaHallFromDb.setSeatColumns(cinemaHall.getSeatColumns());
        }
        if (cinemaHall.getSeatRows() > 0) {
            cinemaHallFromDb.setSeatRows(cinemaHall.getSeatRows());
        }
        return cinemaHallRepository.save(cinemaHallFromDb);
    }

    @Override
    public void deleteById(Long id) {
        cinemaHallRepository.deleteById(id);
    }

//    @Override //add hall for the movie session, movie session has to exist already
//    public MovieSession addHallForTheMovieSession(Long cinemaHallId, Long movieSessionId) {
//        MovieSession movieSession = movieSessionRepository.findById(movieSessionId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie session not found"));
//        CinemaHall cinemaHall = cinemaHallRepository.findById(cinemaHallId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found"));
//        movieSession.setHall(cinemaHall);
//        cinemaHall.getSessions().add(movieSession);
//        cinemaHallRepository.save(cinemaHall);
//        return movieSession;
//    }

    @Override //add seats to the hall (adds all the seats at one time automatically while creating a hall)
    public List<Seat> addSeatsToTheMovieHall(CinemaHall cinemaHall) {
//        CinemaHall cinemaHall = cinemaHallRepository.findById(cinemaHallId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found"));
        int totalSeatsAllowed = cinemaHall.getSeatColumns() * cinemaHall.getSeatRows();
        int existingSeats = cinemaHall.getSeats() != null ? cinemaHall.getSeats().size() : 0;

        if (existingSeats >= totalSeatsAllowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are already enough seats");
        }

        int seatsToAdd = totalSeatsAllowed - existingSeats;
        List<Seat> newSeats = new ArrayList<>();
        for (int i = 0; i < seatsToAdd; i++) {
            Seat seat = Seat.builder()
                    .seatNr(existingSeats + i + 1)
                    .isSeatTaken(false)
                    .hall(cinemaHall)
                    .build();
            cinemaHall.getSeats().add(seat);
            Seat savedSeat = seatRepository.save(seat);
            newSeats.add(savedSeat);
        }
        cinemaHallRepository.save(cinemaHall);
        return newSeats;
    }

}
