package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.Hall;

import java.util.List;

public interface HallRepository extends JpaRepository<Hall, Integer> {
    List<Hall> findAllByCinemaId(Integer id);
    List<Hall> findAllByCinemaIdAndTypeId(Integer id, Integer type);
}
