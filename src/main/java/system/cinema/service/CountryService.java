package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.Country;
import system.cinema.repository.CountryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository)
    {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAll()
    {
        return this.countryRepository.findAll();
    }

    public Optional<Country> getByName(String name)
    {
        return this.countryRepository.findByName(name);
    }

    public Optional<Country> getById(Integer id)
    {
        return this.countryRepository.findById(id);
    }

    public Country save(Country country)
    {
        return this.countryRepository.save(country);
    }

    public void deleteAll()
    {
        this.countryRepository.deleteAll();
    }
}
