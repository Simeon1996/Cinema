package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.HallType;
import system.cinema.repository.HallTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HallTypeService {

    private HallTypeRepository hallTypeRepository;

    public HallTypeService(HallTypeRepository hallTypeRepository)
    {
        this.hallTypeRepository = hallTypeRepository;
    }

    public Optional<HallType> getById(Integer id)
    {
        return this.hallTypeRepository.findById(id);
    }

    public List<HallType> getAll()
    {
        return this.hallTypeRepository.findAll();
    }

    public void deleteAll()
    {
        this.hallTypeRepository.deleteAll();
    }

    public HallType save(HallType type)
    {
        return this.hallTypeRepository.save(type);
    }
}
