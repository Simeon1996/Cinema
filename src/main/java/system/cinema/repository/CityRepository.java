package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    Optional<City> findByName(String name);
}
