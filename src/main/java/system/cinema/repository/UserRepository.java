package system.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.cinema.model.Role;
import system.cinema.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findAllByRole(Role.Unit name);
}
