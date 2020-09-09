package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import system.cinema.utils.assembler.hateoas.MovieModelAssembler;
import system.cinema.exception.CinemaEntityInvalidParameterException;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Hall;
import system.cinema.model.Movie;
import system.cinema.model.MovieType;
import system.cinema.service.HallService;
import system.cinema.service.MovieService;
import system.cinema.service.MovieTypeService;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class MovieController {

    private MovieService movieService;

    private MovieTypeService movieTypeService;

    private HallService hallService;

    private MovieModelAssembler assembler;

    public MovieController(
        MovieService movieService,
        HallService hallService,
        MovieTypeService movieTypeService,
        MovieModelAssembler assembler
    ) {
        this.movieService = movieService;
        this.hallService = hallService;
        this.movieTypeService = movieTypeService;
        this.assembler = assembler;
    }

    @GetMapping("/cinemas/{id}/movies")
    public CollectionModel<EntityModel<Movie>> getMoviesByCinema(
        @PathVariable Integer id,
        @RequestParam(required = false) Integer type
    ) {
        if (type == null) {
            CollectionModel<EntityModel<Movie>> movies = this.assembler.toCollectionModel(
                this.movieService.getAllByCinemaId(id)
            );

            return new CollectionModel<>(movies,
                linkTo(methodOn(MovieController.class).getMoviesByCinema(id, null)).withSelfRel()
            );
        }

        CollectionModel<EntityModel<Movie>> movies = this.assembler.toCollectionModel(
            this.movieService.getAllByCinemaIdAndType(id, type)
        );

        return new CollectionModel<>(movies,
            linkTo(methodOn(MovieController.class).getMoviesByCinema(id, type)).withSelfRel()
        );
    }

    @GetMapping("/movies/{id}")
    public EntityModel<Movie> getById(@PathVariable Integer id)
    {
        Movie movie = this.movieService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The movie was not found."));

        return this.assembler.toModel(movie);
    }

    @PostMapping("/halls/{id}/movies")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> create(@PathVariable Integer id, @Valid @RequestBody Movie movie, @RequestParam(required = true) Integer type)
    {
        Hall hall = this.hallService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hall was not found."));

        MovieType movieType = this.movieTypeService.getById(type)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The movieType was not found."));

        movie.setType(movieType);
        movie.setHall(hall);

        EntityModel<Movie> movieModel = this.assembler.toModel(this.movieService.save(movie));

        return ResponseEntity
            .created(movieModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(movieModel);
    }

    @PutMapping("/movies/{id}")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody Movie newMovie)
    {
        Movie updatedMovie = this.movieService.getById(id)
            .map(movie -> {
                movie.setName(newMovie.getName());
                movie.setRating(newMovie.getRating());
                movie.setStartTime(newMovie.getStartTime());
                movie.setEndTime(newMovie.getEndTime());
                movie.setStartDate(newMovie.getStartDate());

                return movieService.save(movie);
            })
            .orElseGet(() -> {
                newMovie.setId(id);
                return this.movieService.save(newMovie);
            });

        EntityModel<Movie> movieModel = this.assembler.toModel(updatedMovie);

        return ResponseEntity
            .created(movieModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(movieModel);
    }

    @PutMapping("/movies/{movieId}/to-hall/{hallId}")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> changeHall(@PathVariable Integer movieId, @PathVariable Integer hallId)
    {
        Movie movie = this.movieService.getById(movieId)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The movie was not found."));

        if (movie.getHall().getId().equals(hallId)) {
            throw new CinemaEntityInvalidParameterException("The movie is already bounded to this hall.");
        }

        Hall hall = this.hallService.getById(hallId)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hall was not found."));

        movie.setHall(hall);

        EntityModel<Movie> movieModel = this.assembler.toModel(this.movieService.save(movie));

        return ResponseEntity
            .created(movieModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(movieModel);
    }

    @PutMapping("/movies/{movieId}/to-type/{typeId}")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> changeType(@PathVariable Integer movieId, @PathVariable Integer typeId)
    {
        Movie movie = this.movieService.getById(movieId)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The movie was not found."));

        if (movie.getType().getId().equals(typeId)) {
            throw new CinemaEntityInvalidParameterException("The movie is already bounded to the provided type.");
        }

        MovieType movieType = this.movieTypeService.getById(typeId)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The movieType was not found."));

        movie.setType(movieType);

        EntityModel<Movie> movieModel = this.assembler.toModel(this.movieService.save(movie));

        return ResponseEntity
            .created(movieModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(movieModel);
    }

    @DeleteMapping("/movies/{id}")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> delete(@PathVariable Integer id)
    {
        this.movieService.delete(
            this.movieService.getById(id)
                .orElseThrow(() -> new CinemaEntityNotFoundException("The movie was not found."))
        );

        return ResponseEntity.ok().build();
    }
}
