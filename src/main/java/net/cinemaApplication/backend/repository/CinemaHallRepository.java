package net.cinemaApplication.backend.repository;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {
}
