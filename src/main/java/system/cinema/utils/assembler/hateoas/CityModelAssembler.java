package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.CityController;
import system.cinema.model.City;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CityModelAssembler implements RepresentationModelAssembler<City, EntityModel<City>> {

    @Override
    public EntityModel<City> toModel(City city) {
        return new EntityModel<>(city,
            linkTo(methodOn(CityController.class).getById(city.getId())).withSelfRel(),
            linkTo(methodOn(CityController.class).getAll()).withRel("cities")
        );
    }
}
