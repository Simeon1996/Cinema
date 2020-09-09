package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.TicketTypeController;
import system.cinema.model.TicketType;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TicketTypeModelAssembler implements RepresentationModelAssembler<TicketType, EntityModel<TicketType>> {

    @Override
    public EntityModel<TicketType> toModel(TicketType ticketType) {
        return new EntityModel<>(ticketType,
            linkTo(methodOn(TicketTypeController.class).getById(ticketType.getId())).withSelfRel(),
            linkTo(methodOn(TicketTypeController.class).getAll()).withRel("ticket_types")
        );
    }
}
