package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import net.cinemaApplication.backend.repository.CinemaHallRepository;
import net.cinemaApplication.backend.service.services.CinemaHallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CinemaHallImplementation implements CinemaHallService {
    @Autowired
    CinemaHallRepository cinemaHallRepository;
    @Override
    public CinemaHall saveCinemaHall(CinemaHall cinemaHall) {
        return cinemaHallRepository.save(cinemaHall);
    }

    @Override
    public List<CinemaHall> getAllCinemaHalls() {
        return cinemaHallRepository.findAll();
    }

    @Override
    public CinemaHall updateCinemaHall(CinemaHall cinemaHall, Long id) {
        return null; //implementing later
    }

    @Override
    public void deleteById(Long id) {
        cinemaHallRepository.deleteById(id);
    }
}
