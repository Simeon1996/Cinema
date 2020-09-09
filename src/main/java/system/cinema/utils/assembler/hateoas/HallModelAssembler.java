package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.HallController;
import system.cinema.model.Hall;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HallModelAssembler implements RepresentationModelAssembler<Hall, EntityModel<Hall>> {

    @Override
    public EntityModel<Hall> toModel(Hall hall) {
        return new EntityModel<>(hall,
            linkTo(methodOn(HallController.class).getById(hall.getId())).withSelfRel(),
            linkTo(methodOn(HallController.class).getAllByCinemaId(hall.getCinema().getId(), hall.getType().getId())).withRel("halls_by_cinema_and_type"),
            linkTo(methodOn(HallController.class).getAllByCinemaId(hall.getCinema().getId(), null)).withRel("halls_by_cinema")
        );
    }
}