package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import system.cinema.utils.assembler.hateoas.HallModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Hall;
import system.cinema.service.HallService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class HallController {

    private HallService hallService;

    private HallModelAssembler assembler;

    public HallController(HallService hallService, HallModelAssembler assembler)
    {
        this.hallService = hallService;
        this.assembler = assembler;
    }

    @GetMapping("/cinemas/{id}/halls")
    public CollectionModel<EntityModel<Hall>> getAllByCinemaId(@PathVariable Integer id, @RequestParam(required = false) Integer type)
    {
        CollectionModel<EntityModel<Hall>> halls;

        if (type == null) {
            halls = this.assembler.toCollectionModel(this.hallService.getByCinemaId(id));

            return new CollectionModel<>(halls,
                linkTo(methodOn(HallController.class).getAllByCinemaId(id, null)).withSelfRel()
            );
        }

        halls = this.assembler.toCollectionModel(this.hallService.getByCinemaIdAndType(id, type));

        return new CollectionModel<>(halls,
            linkTo(methodOn(HallController.class).getAllByCinemaId(id, type)).withSelfRel()
        );
    }

    @GetMapping("/halls/{id}")
    public EntityModel<Hall> getById(@PathVariable Integer id)
    {
        Hall hall = this.hallService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hall was not found."));

        return this.assembler.toModel(hall);
    }
}
