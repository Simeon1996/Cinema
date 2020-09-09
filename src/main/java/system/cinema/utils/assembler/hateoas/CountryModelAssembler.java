package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.CountryController;
import system.cinema.model.Country;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryModelAssembler implements RepresentationModelAssembler<Country, EntityModel<Country>> {

    @Override
    public EntityModel<Country> toModel(Country country) {
        return new EntityModel<>(country,
            linkTo(methodOn(CountryController.class).getById(country.getId())).withSelfRel(),
            linkTo(methodOn(CountryController.class).getAll()).withRel("countries")
        );
    }
}