package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import net.cinemaApplication.backend.entity.user.Ticket;
import net.cinemaApplication.backend.service.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Operation(summary = "Get all tickets")
    @GetMapping("/tickets")
    public List<Ticket> getAllTickets()
    {
        return ticketService.getAllTickets();
    }

    @Operation(summary = "Get ticket by id")
    @GetMapping("/ticket{id}")
    public Ticket getTicketById(@PathVariable("id") Long id)
    {
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        if (ticket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        return ticket.get();
    }

    @Operation(summary = "Get all tickets for a specific movie session")
    @GetMapping("/movieSession{id}/tickets")
    public List<Ticket> getAllSpecificMovieSessions(@PathVariable("id") Long id)
    {
        return ticketService.getAllTicketsBySession(id);
    }

    @Operation(summary = "Delete a ticket")
    @DeleteMapping("/deleteTicket{id}")
    public String deleteTicketById(@PathVariable("id") Long id)
    {
        ticketService.deleteById(id);
        return "Deleted Successfully";
    }
}
