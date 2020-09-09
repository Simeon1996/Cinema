package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.TicketType;
import system.cinema.repository.TicketTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TicketTypeService {

    private TicketTypeRepository ticketTypeRepository;

    public TicketTypeService(TicketTypeRepository ticketTypeRepository)
    {
        this.ticketTypeRepository = ticketTypeRepository;
    }

    public Optional<TicketType> getById(Integer id)
    {
        return this.ticketTypeRepository.findById(id);
    }

    public void deleteAll()
    {
        this.ticketTypeRepository.deleteAll();
    }

    public TicketType save(TicketType type)
    {
        return this.ticketTypeRepository.save(type);
    }

    public List<TicketType> getAll()
    {
        return this.ticketTypeRepository.findAll();
    }
}
