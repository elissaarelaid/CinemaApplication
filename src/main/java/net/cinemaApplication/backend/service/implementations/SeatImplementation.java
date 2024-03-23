package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.repository.SeatRepository;
import net.cinemaApplication.backend.service.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
public class SeatImplementation implements SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @Override //for updating seat status when someone purchases a ticket or cancels
    public Seat updateSeatStatus(Long id, boolean status) {
        Seat seatFromDb = seatRepository.findById(id).get();
        if (seatFromDb.isSeatTaken() && status) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This seat is already taken");
        }
        seatFromDb.setSeatTaken(status);
        return seatRepository.save(seatFromDb);
    }

    @Override
    public void deleteById(Long id) {
        seatRepository.deleteById(id);
    }
}
