package net.cinemaApplication.backend.repository;

import net.cinemaApplication.backend.entity.user.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
