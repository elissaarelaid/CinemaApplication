package net.cinemaApplication.backend.service.services;

import net.cinemaApplication.backend.entity.user.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService { //you cannot update your ticket (but user can cancel)
    List<Ticket> getAllTickets();
    List<Ticket> getAllTicketsBySession(Long sessionId);
    Optional<Ticket> getTicketById(Long id);
    void deleteById(Long id);
}
