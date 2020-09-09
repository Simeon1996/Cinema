package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.HallTypeController;
import system.cinema.model.HallType;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HallTypeModelAssembler implements RepresentationModelAssembler<HallType, EntityModel<HallType>> {

    @Override
    public EntityModel<HallType> toModel(HallType hallType) {
        return new EntityModel<>(hallType,
            linkTo(methodOn(HallTypeController.class).getById(hallType.getId())).withSelfRel(),
            linkTo(methodOn(HallTypeController.class).getAll()).withRel("hall_types")
        );
    }
}