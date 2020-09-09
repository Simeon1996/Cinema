package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import system.cinema.utils.assembler.hateoas.MovieTypeModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.MovieType;
import system.cinema.service.MovieTypeService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class MovieTypeController {

    private MovieTypeService movieTypeService;

    private MovieTypeModelAssembler assembler;

    public MovieTypeController(MovieTypeService movieTypeService, MovieTypeModelAssembler assembler)
    {
        this.movieTypeService = movieTypeService;
        this.assembler = assembler;
    }

    @GetMapping("/movie-types")
    public CollectionModel<EntityModel<MovieType>> getAll()
    {
        CollectionModel<EntityModel<MovieType>> types = this.assembler.toCollectionModel(
            this.movieTypeService.getAll()
        );

        return new CollectionModel<>(types,
            linkTo(methodOn(MovieTypeController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/movie-types/{id}")
    public EntityModel<MovieType> getById(@PathVariable Integer id)
    {
        MovieType movieType = this.movieTypeService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The movieType was not found."));

        return this.assembler.toModel(movieType);
    }
}
