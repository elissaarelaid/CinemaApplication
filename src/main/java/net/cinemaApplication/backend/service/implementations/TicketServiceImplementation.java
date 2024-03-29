package net.cinemaApplication.backend.service.implementations;

import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.repository.TicketRepository;
import net.cinemaApplication.backend.service.services.MovieSessionService;
import net.cinemaApplication.backend.service.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketServiceImplementation implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieSessionService movieSessionService;
    /**
     *
     * @return all the tickets
     */
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     *
     * @param id by it system finds a ticket
     * @return ticket by id
     */
    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     *
     * @param sessionId represents a session for what to get all the tickets
     * @return list of tickets
     * @throws ResponseStatusException if movie session is not found
     */
    @Override
    public List<Ticket> getAllTicketsBySession(Long sessionId) {
        if (movieSessionService.getMovieSessionById(sessionId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie session not found");
        }
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream().filter(c -> Objects.equals(c.getSession().getId(), sessionId)).toList();
    }

    /**
     * Delete ticket by id
     * @param id of the ticket
     * @throws ResponseStatusException if ticket is not found
     */
    @Override
    public void deleteById(Long id) {
        if (ticketRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        ticketRepository.deleteById(id);
    }
}
