package net.cinemaApplication.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ticket", description = "Operations related to tickets in the cinema system")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Operation(summary = "Get all tickets",
            description = "Retrieves a list of all tickets purchased for all movie sessions.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All tickets successfully returned")
            })
    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }


    @Operation(summary = "Get ticket by id",
            description = "Retrieves details of a specific ticket by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ticket details successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Ticket not found")
            })
    @GetMapping("/ticket{id}")
    public Ticket getTicketById(@PathVariable("id") Long id) {
        Optional<Ticket> ticket = ticketService.getTicketById(id);
        if (ticket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return ticket.get();
    }


    @Operation(summary = "Get all tickets for a specific movie session",
            description = "Retrieves all tickets purchased for a specific movie session.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tickets for the specific movie session successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Movie session not found")
            })
    @GetMapping("/movieSession{id}/tickets")
    public List<Ticket> getAllSpecificMovieSessions(@PathVariable("id") Long id) {
        return ticketService.getAllTicketsBySession(id);
    }

    @Operation(summary = "Delete a ticket",
            description = "Deletes a specific ticket by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ticket successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Ticket not found")
            })
    @DeleteMapping("/deleteTicket{id}")
    public String deleteTicketById(@PathVariable("id") Long id) {
        ticketService.deleteById(id);
        return "Deleted Successfully";
    }

}
