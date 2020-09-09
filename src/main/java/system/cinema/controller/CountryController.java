package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.utils.assembler.hateoas.CountryModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Country;
import system.cinema.service.CountryService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CountryController {

    private CountryService countryService;

    private CountryModelAssembler assembler;

    public CountryController(CountryService countryService, CountryModelAssembler assembler)
    {
        this.countryService = countryService;
        this.assembler = assembler;
    }

    @GetMapping("/countries")
    public CollectionModel<EntityModel<Country>> getAll()
    {
        CollectionModel<EntityModel<Country>> countries = this.assembler.toCollectionModel(
            this.countryService.getAll()
        );

        return new CollectionModel<>(countries,
            linkTo(methodOn(CountryController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/countries/{id}")
    public EntityModel<Country> getById(@PathVariable Integer id) {
        Country country = this.countryService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The country was not found."));

        return this.assembler.toModel(country);
    }
}
