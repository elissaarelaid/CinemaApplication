package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.repository.TicketRepository;
import net.cinemaApplication.backend.service.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketServiceImplementation implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }
    @Override
    public List<Ticket> getAllTicketsBySession(Long sessionId) {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream().filter(c -> Objects.equals(c.getSession().getId(), sessionId)).toList();
    }

    @Override
    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }
}
