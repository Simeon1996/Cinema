package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import system.cinema.utils.assembler.hateoas.UserModelAssembler;
import system.cinema.exception.CinemaEntityInvalidParameterException;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Role;
import system.cinema.model.User;
import system.cinema.service.RoleService;
import system.cinema.service.UserService;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Secured("ROLE_ADMIN")
public class UserController {

    private UserService userService;

    private RoleService roleService;

    private UserModelAssembler assembler;

    public UserController(
        UserService userService,
        UserModelAssembler assembler,
        RoleService roleService
    ) {
        this.userService = userService;
        this.assembler = assembler;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAll()
    {
        CollectionModel<EntityModel<User>> users = this.assembler.toCollectionModel(
            this.userService.getAll()
        );

        return new CollectionModel<>(users,
            linkTo(methodOn(UserController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> getById(@PathVariable Integer id)
    {
        return this.assembler.toModel(
            this.userService.getById(id)
                .orElseThrow(() -> new CinemaEntityNotFoundException("The user was not found."))
        );
    }

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestParam Integer roleId, @Valid @RequestBody User user)
    {
        this.roleService.getById(roleId)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The role was not found."));

        EntityModel<User> userModel = this.assembler.toModel(this.userService.save(user));

        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody User user)
    {
        User updatedUser = this.userService.getById(id)
            .map(usr -> {
                usr.setUsername(user.getUsername());
                usr.setPassword(user.getPassword());

                return this.userService.save(usr);
            })
            .orElseGet(() -> {
                user.setId(id);
                return this.userService.save(user);
            });

        EntityModel<User> userModel = assembler.toModel(updatedUser);

        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }

    @PutMapping("/users/{id}/to-role/{roleId}")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer id, @PathVariable Integer roleId)
    {
        User user = this.userService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The user was not found."));

        if (user.getRole().getId().equals(roleId)) {
            throw new CinemaEntityInvalidParameterException("The user is already bounded to this role.");
        }

        Role role = this.roleService.getById(roleId)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The role was not found."));

        user.setRole(role);

        EntityModel<User> userModel = assembler.toModel(
            this.userService.save(user)
        );

        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id)
    {
        this.userService.delete(
            this.userService.getById(id)
                .orElseThrow(() -> new CinemaEntityNotFoundException("The user was not found."))
        );

        return ResponseEntity.ok().build();
    }
}
