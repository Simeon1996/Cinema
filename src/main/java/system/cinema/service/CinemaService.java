package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.Cinema;
import system.cinema.repository.CinemaRepository;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
public class CinemaService {

    private CinemaRepository cinemaRepository;

    public CinemaService(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }

    public List<Cinema> getAll()
    {
        return this.cinemaRepository.findAll();
    }

    public Optional<Cinema> getById(Integer id)
    {
        return this.cinemaRepository.findById(id);
    }

    public Cinema save(Cinema cinema)
    {
        return this.cinemaRepository.save(cinema);
    }

    public Optional<Cinema> getByName(String name)
    {
        return this.cinemaRepository.findOneByName(name);
    }

    public void deleteAll()
    {
        this.cinemaRepository.deleteAll();
    }

//    public Cinema getCinemaByName(String name)
//    {
//        return this.cinemaRepository.findOneByName(name).orElse(null);
//    }
}
