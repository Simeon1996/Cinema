package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.City;
import system.cinema.model.Country;
import system.cinema.repository.CityRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private CityRepository cityRepository;

    public CityService(CityRepository cityRepository)
    {
        this.cityRepository = cityRepository;
    }

    public List<City> getAll()
    {
        return this.cityRepository.findAll();
    }

    public Optional<City> getById(Integer id)
    {
        return this.cityRepository.findById(id);
    }

    public City save(City city)
    {
        return this.cityRepository.save(city);
    }

    public Optional<City> getByName(String name)
    {
        return this.cityRepository.findByName(name);
    }

    public void deleteAll()
    {
        this.cityRepository.deleteAll();
    }
}
