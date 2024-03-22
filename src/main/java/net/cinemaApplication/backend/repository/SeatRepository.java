package net.cinemaApplication.backend.repository;

import net.cinemaApplication.backend.entity.cinemaHall.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
