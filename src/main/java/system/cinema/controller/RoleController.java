package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.utils.assembler.hateoas.RoleModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Role;
import system.cinema.service.RoleService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RoleController {

    private RoleService roleService;

    private RoleModelAssembler assembler;

    public RoleController(RoleService roleService, RoleModelAssembler assembler)
    {
        this.roleService = roleService;
        this.assembler = assembler;
    }

    @GetMapping("/roles")
    public CollectionModel<EntityModel<Role>> getAll()
    {
        CollectionModel<EntityModel<Role>> roles = this.assembler.toCollectionModel(
            this.roleService.getAll()
        );

        return new CollectionModel<>(roles,
            linkTo(methodOn(RoleController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/roles/{id}")
    public EntityModel<Role> getById(@PathVariable Integer id)
    {
        Role role = this.roleService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The role was not found."));

        return this.assembler.toModel(role);
    }
}
