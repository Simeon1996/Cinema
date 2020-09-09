package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.RoleController;
import system.cinema.model.Role;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoleModelAssembler implements RepresentationModelAssembler<Role, EntityModel<Role>> {

    @Override
    public EntityModel<Role> toModel(Role role) {
        return new EntityModel<>(role,
            linkTo(methodOn(RoleController.class).getById(role.getId())).withSelfRel(),
            linkTo(methodOn(RoleController.class).getAll()).withRel("roles")
        );
    }
}
