package system.cinema.service;

import org.springframework.stereotype.Service;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Cinema;
import system.cinema.model.Hall;
import system.cinema.model.HallType;
import system.cinema.repository.HallRepository;
import system.cinema.repository.HallTypeRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class HallService {

    private HallRepository hallRepository;

    private HallTypeRepository hallTypeRepository;

    public HallService(HallRepository hallRepository, HallTypeRepository hallTypeRepository)
    {
        this.hallRepository = hallRepository;
        this.hallTypeRepository = hallTypeRepository;
    }

    public List<Hall> getByCinemaId(Integer id)
    {
        return this.hallRepository.findAllByCinemaId(id);
    }

    /**
     * Get halls by cinema Id and Type
     *
     * @param id Cinema Id
     * @param type The type which is limited to number of values.
     *
     * @return Collection of Halls
     *
     * @throws CinemaEntityNotFoundException is thrown whenever a hall was not found
     */
    public List<Hall> getByCinemaIdAndType(Integer id, Integer type) throws CinemaEntityNotFoundException
    {
        this.hallTypeRepository.findById(type)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hallType was not found."));

        return this.hallRepository.findAllByCinemaIdAndTypeId(id, type);
    }

    /**
     * Get free slots number of the specific hall
     *
     * @param id Hall ID
     *
     * @TODO remove the method due to no usages
     *
     * @return number of free slots for the hall
     */
    public int getFreeSlots(Integer id) throws CinemaEntityNotFoundException
    {
        Hall hall = this.hallRepository.findById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hall was not found."));

        int totalSeats = hall.getRows() * hall.getSeatsPerRow();

        return totalSeats - hall.getSlots().size();
    }

    /**
     * Save a number of halls of a certain types within a specific cinema
     *
     * @param cinemaData All the required data that a cinema has like number of halls, types and their capacity
     * @param cinema The corresponding cinema entity
     */
    public void saveHalls(Cinema.Unit cinemaData, Cinema cinema)
    {
        AtomicReference<Short> hallsCounter = new AtomicReference<>((short) 0);

        cinemaData.getHalls().forEach((type, data) -> {
            if (!data.keySet().containsAll(Set.of("halls", "rows", "seats"))) {
                throw new MissingFormatArgumentException("The required arguments for the action are not present.");
            }

            final short rows = data.get("rows");
            final short seats = data.get("seats");
            final HallType hallType = hallTypeRepository.findByType(type.toString());

            short hallsCount = data.get("halls");
            short currentHallNumber = 1;
            for (short i = 1; i <= hallsCount; i++) {
                currentHallNumber = (short) (hallsCounter.get() + i);

                this.hallRepository.save(new Hall(currentHallNumber, rows, seats, hallType, cinema));
            }

            hallsCounter.set(currentHallNumber);
        });
    }

    public Optional<Hall> getById(Integer id)
    {
        return this.hallRepository.findById(id);
    }
}
