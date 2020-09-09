package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import system.cinema.model.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Movie findByName(String name);

    @Query("SELECT m FROM Movie m INNER JOIN m.hall h INNER JOIN h.cinema c WHERE c.id = ?1")
    List<Movie> getAllByCinemaId(Integer id);

    @Query("SELECT m FROM Movie m INNER JOIN m.hall h INNER JOIN h.cinema c WHERE c.id = ?1 AND m.type.id = ?2")
    List<Movie> getAllByCinemaIdAndType(Integer id, Integer typeId);
}
