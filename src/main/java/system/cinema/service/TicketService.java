package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.model.Ticket;
import system.cinema.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository)
    {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllByMovieId(Integer id)
    {
        return this.ticketRepository.findAllByMovieId(id);
    }

    public List<Ticket> getAllByMovieIdAndTypeId(Integer id, Integer typeId)
    {
        return this.ticketRepository.findAllByMovieIdAndTypeId(id, typeId);
    }

    public Optional<Ticket> getById(Integer id)
    {
        return this.ticketRepository.findById(id);
    }

    public void delete(Ticket ticket)
    {
        this.ticketRepository.delete(ticket);
    }

    public void deleteAll()
    {
        this.ticketRepository.deleteAll();
    }

    public Ticket save(Ticket ticket)
    {
        return this.ticketRepository.save(ticket);
    }
}
