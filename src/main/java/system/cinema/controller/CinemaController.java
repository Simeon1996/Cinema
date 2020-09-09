package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import system.cinema.utils.assembler.hateoas.CinemaModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Cinema;
import system.cinema.service.CinemaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CinemaController {

    private CinemaService cinemaService;

    private CinemaModelAssembler assembler;

    /**
     * @param cinemaService Cinema service
     * @param assembler HATEOAS assembler
     */
    public CinemaController(CinemaService cinemaService, CinemaModelAssembler assembler)
    {
        this.cinemaService = cinemaService;
        this.assembler = assembler;
    }

    @GetMapping({"/cinemas", "/"})
    public CollectionModel<EntityModel<Cinema>> getAll()
    {
        CollectionModel<EntityModel<Cinema>> cinemas =
            assembler.toCollectionModel(this.cinemaService.getAll());

        return new CollectionModel<>(cinemas,
            linkTo(methodOn(CinemaController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/cinemas/{id}")
    public EntityModel<Cinema> getById(@PathVariable Integer id)
    {
        Cinema cinema = this.cinemaService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The cinema was not found."));

        return assembler.toModel(cinema);
    }
}
