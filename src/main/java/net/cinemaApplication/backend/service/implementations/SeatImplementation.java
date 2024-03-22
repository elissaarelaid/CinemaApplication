package net.cinemaApplication.backend.service.implementations;

import lombok.Setter;
import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import net.cinemaApplication.backend.repository.SeatRepository;
import net.cinemaApplication.backend.service.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SeatImplementation implements SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Override
    public Seat saveSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @Override
    public Seat updateSeat(Seat seat, Long id) {
        return null; //implementing later
    }

    @Override
    public void deleteById(Long id) {
        seatRepository.deleteById(id);
    }
}
