package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.HallSlot;

import java.util.List;
import java.util.Optional;

public interface HallSlotRepository extends JpaRepository<HallSlot, Integer> {
    List<HallSlot> findAllByHallId(Integer id);
    Optional<HallSlot> findByIdAndHallId(Integer id, Integer hallId);
}
