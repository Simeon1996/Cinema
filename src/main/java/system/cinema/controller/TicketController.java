package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import system.cinema.utils.assembler.hateoas.TicketModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Ticket;
import system.cinema.service.MovieService;
import system.cinema.service.TicketService;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TicketController {

    private TicketService ticketService;

    private MovieService movieService;

    private TicketModelAssembler assembler;

    public TicketController(TicketService ticketService, MovieService movieService, TicketModelAssembler assembler)
    {
        this.ticketService = ticketService;
        this.movieService = movieService;
        this.assembler = assembler;
    }

    @GetMapping("/movies/{id}/tickets")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public CollectionModel<EntityModel<Ticket>> getTicketsByMovieId(
        @PathVariable Integer id,
        @RequestParam(required = false) Integer type
    ) {
        if (type == null) {
            CollectionModel<EntityModel<Ticket>> tickets = this.assembler.toCollectionModel(
                    this.ticketService.getAllByMovieId(id)
            );

            return new CollectionModel<>(tickets,
                linkTo(methodOn(TicketController.class).getTicketsByMovieId(id, null)).withSelfRel()
            );
        }

        CollectionModel<EntityModel<Ticket>> tickets = this.assembler.toCollectionModel(
            this.ticketService.getAllByMovieIdAndTypeId(id, type)
        );

        return new CollectionModel<>(tickets,
            linkTo(methodOn(TicketController.class).getTicketsByMovieId(id, type)).withSelfRel()
        );
    }

    @GetMapping("/tickets/{id}")
    public EntityModel<Ticket> getById(@PathVariable Integer id)
    {
        Ticket ticket = this.ticketService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The ticket was not found."));

        return this.assembler.toModel(ticket);
    }

    @PostMapping("/movies/{movieId}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@PathVariable Integer movieId, @Valid @RequestBody Ticket ticket)
    {
        ticket.setMovie(
            this.movieService.getById(movieId)
                .orElseThrow(() -> new CinemaEntityNotFoundException("The movie was not found"))
        );

        EntityModel<Ticket> ticketModel = this.assembler.toModel(this.ticketService.save(ticket));

        return ResponseEntity
            .created(ticketModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(ticketModel);
    }

    @DeleteMapping("/tickets/{id}")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> delete(@PathVariable Integer id)
    {
        this.ticketService.delete(
            this.ticketService.getById(id)
                .orElseThrow(() -> new CinemaEntityNotFoundException("The ticket was not found."))
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/tickets/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody Ticket newTicket)
    {
        Ticket updatedTicket = this.ticketService.getById(id)
            .map(ticket -> {
                ticket.setBelongsTo(newTicket.getBelongsTo());
                ticket.setAge(newTicket.getAge());
                ticket.setEmail(newTicket.getEmail());
                ticket.setPhone(newTicket.getPhone());
                ticket.setRow(newTicket.getRow());
                ticket.setSeat(newTicket.getSeat());

                return ticketService.save(ticket);
            })
            .orElseGet(() -> {
                newTicket.setId(id);
                return this.ticketService.save(newTicket);
            });

        EntityModel<Ticket> ticketModel = this.assembler.toModel(updatedTicket);

        return ResponseEntity
            .created(ticketModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(ticketModel);
    }
}
