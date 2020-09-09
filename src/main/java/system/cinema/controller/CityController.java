package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.utils.assembler.hateoas.CityModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.City;
import system.cinema.service.CityService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CityController {

    private CityService cityService;

    private CityModelAssembler assembler;

    public CityController(CityService cityService, CityModelAssembler assembler)
    {
        this.cityService = cityService;
        this.assembler = assembler;
    }

    @GetMapping("/cities")
    public CollectionModel<EntityModel<City>> getAll()
    {
        CollectionModel<EntityModel<City>> cities = assembler.toCollectionModel(
            this.cityService.getAll()
        );

        return new CollectionModel<>(cities,
            linkTo(methodOn(CityController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/cities/{id}")
    public EntityModel<City> getById(@PathVariable Integer id)
    {
        City city = this.cityService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The city was not found."));

        return this.assembler.toModel(city);
    }
}