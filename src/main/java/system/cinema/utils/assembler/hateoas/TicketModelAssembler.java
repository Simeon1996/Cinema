package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.TicketController;
import system.cinema.model.Ticket;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TicketModelAssembler implements RepresentationModelAssembler<Ticket, EntityModel<Ticket>> {

    @Override
    public EntityModel<Ticket> toModel(Ticket ticket) {
        return new EntityModel<>(ticket,
            linkTo(methodOn(TicketController.class).getById(ticket.getId())).withSelfRel(),
            linkTo(methodOn(TicketController.class).getTicketsByMovieId(ticket.getMovie().getId(), null))
                .withRel("tickets_by_movie"),
            linkTo(methodOn(TicketController.class).getTicketsByMovieId(ticket.getMovie().getId(), ticket.getType().getId()))
                .withRel("tickets_by_movie_and_type")
        );
    }
}
