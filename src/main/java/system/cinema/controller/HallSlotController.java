package system.cinema.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import system.cinema.utils.assembler.hateoas.HallSlotModelAssembler;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.Hall;
import system.cinema.model.HallSlot;
import system.cinema.service.HallService;
import system.cinema.service.HallSlotService;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class HallSlotController {

    private HallSlotService hallSlotService;

    private HallService hallService;

    private HallSlotModelAssembler assembler;

    public HallSlotController(HallSlotService hallSlotService, HallService hallService, HallSlotModelAssembler assembler)
    {
        this.hallSlotService = hallSlotService;
        this.hallService = hallService;
        this.assembler = assembler;
    }

    @GetMapping("/halls/{id}/slots")
    public CollectionModel<EntityModel<HallSlot>> getAllByHall(@PathVariable Integer id)
    {
        CollectionModel<EntityModel<HallSlot>> slots = this.assembler.toCollectionModel(
            this.hallSlotService.getAllByHall(id)
        );

        return new CollectionModel<>(slots,
            linkTo(methodOn(HallSlotController.class).getAllByHall(id)).withSelfRel()
        );
    }

    @PostMapping("/halls/{id}/slots")
    public ResponseEntity<?> create(@PathVariable Integer id, @Valid @RequestBody HallSlot hallSlot)
    {
        Hall hall = this.hallService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hall was not found."));

        hallSlot.setHall(hall);

        EntityModel<HallSlot> slotModel = this.assembler.toModel(this.hallSlotService.save(hallSlot));

        return ResponseEntity
            .created(slotModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(slotModel);
    }

    @GetMapping("/slots/{id}")
    public EntityModel<HallSlot> getById(@PathVariable int id)
    {
        HallSlot slot = this.hallSlotService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hallSlot was not found."));

        return this.assembler.toModel(slot);
    }

    @PutMapping("/slots/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody HallSlot hallSlot)
    {
        HallSlot updatedSlot = this.hallSlotService.getById(id)
            .map(slot -> {
                slot.setRow(hallSlot.getRow());
                slot.setSeat(hallSlot.getSeat());

                return this.hallSlotService.save(slot);
            })
            .orElseGet(() -> {
                hallSlot.setId(id);
               return this.hallSlotService.save(hallSlot);
            });

        EntityModel<HallSlot> hallSlotModel = assembler.toModel(updatedSlot);

        return ResponseEntity
            .created(hallSlotModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(hallSlotModel);
    }

    @GetMapping(value = "/halls/{id}/taken-slots", produces = "application/json")
    public MultiValueMap<String, String> getTakenSlots(@PathVariable Integer id)
    {
        Hall hall = this.hallService.getById(id)
            .orElseThrow(() -> new CinemaEntityNotFoundException("The hall was not found."));

        return this.hallSlotService.getTakenSlotsByHall(hall);
    }

    @GetMapping("/halls/{id}/free-slots")
    public Integer getHallFreeSlotsCount(@PathVariable Integer id)
    {
        return this.hallService.getFreeSlots(id);
    }

    @DeleteMapping("/slots/{id}")
    @Secured({ "ROLE_ADMIN", "ROLE_EDITOR" })
    public ResponseEntity<?> delete(@PathVariable Integer id)
    {
         this.hallSlotService.delete(
             this.hallSlotService.getById(id)
                 .orElseThrow(() -> new CinemaEntityNotFoundException("The hallSlot was not found."))
         );

         return ResponseEntity.ok().build();
    }
}
