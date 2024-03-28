package net.cinemaApplication.backend.repository;

import net.cinemaApplication.backend.entity.cinemaHall.CinemaHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {
    /**
     *
     * @param hallNr that represents a unique cinema hall number
     * @return a cinema hall if there already exists a hall with this number
     */
    Optional<CinemaHall> findByHallNr(int hallNr);
}
