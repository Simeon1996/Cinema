package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.CinemaController;
import system.cinema.model.Cinema;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CinemaModelAssembler implements RepresentationModelAssembler<Cinema, EntityModel<Cinema>> {

    @Override
    public EntityModel<Cinema> toModel(Cinema cinema) {
        return new EntityModel<>(cinema,
            linkTo(methodOn(CinemaController.class).getById(cinema.getId())).withSelfRel(),
            linkTo(methodOn(CinemaController.class).getAll()).withRel("cinemas")
        );
    }
}
