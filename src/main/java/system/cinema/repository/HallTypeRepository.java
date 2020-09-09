package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.HallType;

public interface HallTypeRepository extends JpaRepository<HallType, Integer> {
    HallType findByType(String type);
}
