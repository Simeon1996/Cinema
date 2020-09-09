package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import system.cinema.model.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t INNER JOIN t.movie m WHERE m.id = ?1")
    List<Ticket> findAllByMovieId(Integer id);

    @Query("SELECT t FROM Ticket t INNER JOIN t.movie m WHERE m.id = ?1 AND t.type = ?2")
    List<Ticket> findAllByMovieIdAndTypeId(Integer id, Integer typeId);
}
