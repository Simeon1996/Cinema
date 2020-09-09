package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.MovieType;
import system.cinema.repository.MovieTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MovieTypeService {

    private MovieTypeRepository movieTypeRepository;

    public MovieTypeService(MovieTypeRepository movieTypeRepository)
    {
        this.movieTypeRepository = movieTypeRepository;
    }

    public List<MovieType> getAll()
    {
        return this.movieTypeRepository.findAll();
    }

    public Optional<MovieType> getById(Integer id)
    {
        return this.movieTypeRepository.findById(id);
    }

    public void deleteAll()
    {
        this.movieTypeRepository.deleteAll();
    }

    public MovieType save(MovieType type)
    {
        return this.movieTypeRepository.save(type);
    }

    public Optional<MovieType> getByType(String type)
    {
        return this.movieTypeRepository.findOneByType(type);
    }
}
