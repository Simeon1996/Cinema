package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.MovieType;

import java.util.Optional;

public interface MovieTypeRepository extends JpaRepository<MovieType, Integer> {
    Optional<MovieType> findOneByType(String type);
}
