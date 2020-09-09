package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.MovieTypeController;
import system.cinema.model.MovieType;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MovieTypeModelAssembler implements RepresentationModelAssembler<MovieType, EntityModel<MovieType>> {

    @Override
    public EntityModel<MovieType> toModel(MovieType movieType) {
        return new EntityModel<>(movieType,
            linkTo(methodOn(MovieTypeController.class).getById(movieType.getId())).withSelfRel(),
            linkTo(methodOn(MovieTypeController.class).getAll()).withRel("movie_types")
        );
    }
}
