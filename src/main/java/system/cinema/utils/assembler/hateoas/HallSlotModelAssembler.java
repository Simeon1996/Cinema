package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.HallSlotController;
import system.cinema.model.HallSlot;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HallSlotModelAssembler implements RepresentationModelAssembler<HallSlot, EntityModel<HallSlot>> {

    @Override
    public EntityModel<HallSlot> toModel(HallSlot slot) {
        return new EntityModel<>(slot,
            linkTo(methodOn(HallSlotController.class).getById(slot.getId())).withSelfRel(),
            linkTo(methodOn(HallSlotController.class).getAllByHall(slot.getHall().getId())).withRel("slots_by_hall")
        );
    }
}