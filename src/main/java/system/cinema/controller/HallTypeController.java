package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.utils.assembler.hateoas.HallTypeModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.HallType;
import system.cinema.service.HallTypeService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class HallTypeController {

    private HallTypeService hallTypeService;

    private HallTypeModelAssembler assembler;

    public HallTypeController(HallTypeService hallTypeService, HallTypeModelAssembler assembler)
    {
        this.hallTypeService = hallTypeService;
        this.assembler = assembler;
    }

    @GetMapping("/hall-types")
    public CollectionModel<EntityModel<HallType>> getAll()
    {
        CollectionModel<EntityModel<HallType>> hallTypes = this.assembler.toCollectionModel(
            this.hallTypeService.getAll()
        );

        return new CollectionModel<>(hallTypes,
            linkTo(methodOn(HallTypeController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/hall-types/{id}")
    public EntityModel<HallType> getById(@PathVariable Integer id)
    {
        HallType hallType = this.hallTypeService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hallType was not found."));

        return this.assembler.toModel(hallType);
    }
}
