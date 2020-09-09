package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.Cinema;

import java.util.Optional;

public interface CinemaRepository extends JpaRepository<Cinema, Integer> {
    Optional<Cinema> findOneByName(String name);
}
