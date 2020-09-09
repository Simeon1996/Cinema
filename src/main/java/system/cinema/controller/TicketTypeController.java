package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.utils.assembler.hateoas.TicketTypeModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.TicketType;
import system.cinema.service.TicketTypeService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TicketTypeController {

    private TicketTypeService ticketTypeService;

    private TicketTypeModelAssembler assembler;

    public TicketTypeController(TicketTypeService ticketTypeService, TicketTypeModelAssembler assembler)
    {
        this.ticketTypeService = ticketTypeService;
        this.assembler = assembler;
    }

    @GetMapping("/ticket-types/{id}")
    public EntityModel<TicketType> getById(@PathVariable Integer id)
    {
        TicketType type = this.ticketTypeService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The ticketType was not found."));

        return this.assembler.toModel(type);
    }

    @GetMapping("/ticket-types")
    public CollectionModel<EntityModel<TicketType>> getAll()
    {
        CollectionModel<EntityModel<TicketType>> types = this.assembler.toCollectionModel(
            this.ticketTypeService.getAll()
        );

        return new CollectionModel<>(types,
            linkTo(methodOn(TicketTypeController.class).getAll()).withSelfRel()
        );
    }
}
