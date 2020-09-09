package system.cinema.utils.assembler.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import system.cinema.controller.MovieController;
import system.cinema.model.Movie;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MovieModelAssembler implements RepresentationModelAssembler<Movie, EntityModel<Movie>> {

    @Override
    public EntityModel<Movie> toModel(Movie movie) {
        return new EntityModel<>(movie,
            linkTo(methodOn(MovieController.class).getById(movie.getId())).withSelfRel(),
            linkTo(methodOn(MovieController.class).getMoviesByCinema(movie.getHall().getCinema().getId(), null)).withRel("movies_by_cinema"),
            linkTo(methodOn(MovieController.class).getMoviesByCinema(movie.getHall().getCinema().getId(), movie.getType().getId()))
                .withRel("movies_by_cinema_and_type")
        );
    }
}
