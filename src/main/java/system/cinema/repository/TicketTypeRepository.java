package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.TicketType;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {
    TicketType findOneByType(String type);
}
