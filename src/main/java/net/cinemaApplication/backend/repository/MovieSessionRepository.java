package net.cinemaApplication.backend.repository;

import net.cinemaApplication.backend.entity.movieSession.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {
}
