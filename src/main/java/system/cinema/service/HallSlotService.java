package system.cinema.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import system.cinema.model.Hall;
import system.cinema.model.HallSlot;
import system.cinema.repository.HallSlotRepository;

import java.util.List;
import java.util.Optional;

@Service
public class HallSlotService {

    private HallSlotRepository hallSlotRepository;

    public HallSlotService(HallSlotRepository hallSlotRepository)
    {
        this.hallSlotRepository = hallSlotRepository;
    }

    public HallSlot save(HallSlot slot)
    {
        return this.hallSlotRepository.save(slot);
    }

    public List<HallSlot> getAllByHall(Integer id)
    {
        return this.hallSlotRepository.findAllByHallId(id);
    }

    public MultiValueMap<String, String> getTakenSlotsByHall(Hall hall)
    {
        MultiValueMap<String, String> takenSlots = new LinkedMultiValueMap<>();

        hall.getSlots().forEach(
            (slot) -> takenSlots.add(String.valueOf(slot.getRow()), String.valueOf(slot.getSeat()))
        );

        return takenSlots;
    }

    public Optional<HallSlot> getById(Integer id)
    {
        return this.hallSlotRepository.findById(id);
    }

    public Optional<HallSlot> getByIdAndHallId(Integer id, Integer hallId)
    {
        return this.hallSlotRepository.findByIdAndHallId(id, hallId);
    }

    public void delete(HallSlot slot)
    {
        this.hallSlotRepository.delete(slot);
    }
}
