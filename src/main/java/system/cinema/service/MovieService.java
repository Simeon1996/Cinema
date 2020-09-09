package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.Movie;
import system.cinema.repository.MovieRepository;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository)
    {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllByCinemaId(Integer id)
    {
        return this.movieRepository.getAllByCinemaId(id);
    }

    public List<Movie> getAllByCinemaIdAndType(Integer id, Integer type)
    {
        return this.movieRepository.getAllByCinemaIdAndType(id, type);
    }

    public Optional<Movie> getById(Integer id)
    {
        return this.movieRepository.findById(id);
    }

    public Movie save(Movie movie)
    {
        return this.movieRepository.save(movie);
    }

    public void deleteAll()
    {
        this.movieRepository.deleteAll();
    }

    // @TODO Fetch the movies from external source like excel file or smth.
    // Hardcoded for now
    public void loadMovies()
    {

    }

    public void delete(Movie movie)
    {
        this.movieRepository.delete(movie);
    }
}
