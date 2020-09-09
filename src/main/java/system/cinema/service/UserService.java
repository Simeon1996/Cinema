package system.cinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import system.cinema.model.Role;
import system.cinema.model.User;
import system.cinema.model.UserPrincipal;
import system.cinema.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public List<User> getAll()
    {
        return this.userRepository.findAll();
    }

    public List<User> getAllByRole(Role.Unit name)
    {
        return this.userRepository.findAllByRole(name);
    }

    public Optional<User> getById(Integer id)
    {
        return this.userRepository.findById(id);
    }

    public void delete(User user)
    {
        this.userRepository.delete(user);
    }

    public void deleteAll()
    {
        this.userRepository.deleteAll();
    }

    /**
     * It's mandatory to store users within the database using this method which is
     * also responsible for hashing your password with a predefined algorithm
     *
     * @param user The user model
     *
     * @return User saved entity
     */
    public User save(User user)
    {
        user.setPassword(
            this.passwordEncoder.encode(user.getPassword())
        );

        return this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user  = this.userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipal(user);
    }
}
