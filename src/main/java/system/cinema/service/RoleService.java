package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.Role;
import system.cinema.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository)
    {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getById(Integer id)
    {
        return this.roleRepository.findById(id);
    }

    public Optional<Role> getByName(Role.Unit name)
    {
        return this.roleRepository.findByName(name);
    }

    public void deleteAll()
    {
        this.roleRepository.deleteAll();
    }

    public Role save(Role role)
    {
        return this.roleRepository.save(role);
    }

    public List<Role> getAll()
    {
        return this.roleRepository.findAll();
    }
}
