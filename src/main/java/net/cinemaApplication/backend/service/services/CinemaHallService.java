package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;

import java.util.List;

public interface CinemaHallService {
    CinemaHall saveCinemaHall(CinemaHall cinemaHall);
    List<CinemaHall> getAllCinemaHalls();
    CinemaHall updateCinemaHall(CinemaHall cinemaHall, Long id);
    void deleteById(Long id);
}
