package system.cinema.mvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import system.cinema.model.*;
import system.cinema.service.HallService;
import system.cinema.service.HallSlotService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class HallSlotControllerTest {

    private static final String BASE_SLOTS_PATH = "http://localhost/halls/1/slots";

    private static final String BASE_SLOT_PATH = "http://localhost/slots";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HallSlotService hallSlotService;

    @MockBean
    private HallService hallService;

    @Test
    @WithMockUser
    public void testGetSlotsByHall() throws Exception
    {
        List<HallSlot> slots = new ArrayList<>(3);

        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        slots.add(new HallSlot(1, hall, (short) 1, (short) 1));
        slots.add(new HallSlot(2, hall, (short) 1, (short) 2));
        slots.add(new HallSlot(3, hall, (short) 1, (short) 3));

        when(hallSlotService.getAllByHall(1)).thenReturn(slots);

        mockMvc.perform(get("/halls/1/slots"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.hallSlotList", hasSize(3)))
            .andExpect(jsonPath("$._embedded.hallSlotList[0].id").value(1))
            .andExpect(jsonPath("$._embedded.hallSlotList[0].row").value(1))
            .andExpect(jsonPath("$._embedded.hallSlotList[0].seat").value(1))
            .andExpect(jsonPath("$._embedded.hallSlotList[0]._links.self.href").value(BASE_SLOT_PATH + "/" + 1))
            .andExpect(jsonPath("$._embedded.hallSlotList[0]._links.slots_by_hall.href").value(BASE_SLOTS_PATH))
            .andExpect(jsonPath("$._embedded.hallSlotList[1].id").value(2))
            .andExpect(jsonPath("$._embedded.hallSlotList[1].row").value(1))
            .andExpect(jsonPath("$._embedded.hallSlotList[1].seat").value(2))
            .andExpect(jsonPath("$._embedded.hallSlotList[1]._links.self.href").value(BASE_SLOT_PATH + "/" + 2))
            .andExpect(jsonPath("$._embedded.hallSlotList[1]._links.slots_by_hall.href").value(BASE_SLOTS_PATH))
            .andExpect(jsonPath("$._embedded.hallSlotList[2].id").value(3))
            .andExpect(jsonPath("$._embedded.hallSlotList[2].row").value(1))
            .andExpect(jsonPath("$._embedded.hallSlotList[2].seat").value(3))
            .andExpect(jsonPath("$._embedded.hallSlotList[2]._links.self.href").value(  BASE_SLOT_PATH + "/" + 3))
            .andExpect(jsonPath("$._embedded.hallSlotList[2]._links.slots_by_hall.href").value(BASE_SLOTS_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_SLOTS_PATH));
    }

    @Test
    public void testGetSlotsByHallWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/halls/1/slots"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetSlotById() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        HallSlot slot = new HallSlot(1, hall, (short) 1, (short) 1);

        when(hallSlotService.getById(1)).thenReturn(Optional.of(slot));

        mockMvc.perform(get("/slots/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.row").value(1))
            .andExpect(jsonPath("$.seat").value(1))
            .andExpect(jsonPath("$._links.self.href").value(BASE_SLOT_PATH + "/" + 1))
            .andExpect(jsonPath("$._links.slots_by_hall.href").value(BASE_SLOTS_PATH));
    }

    @Test
    public void testGetSlotByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/slots/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetUnknownSlotById() throws Exception
    {
        mockMvc.perform(get("/slots/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The hallSlot was not found."));
    }

    @Test
    @WithMockUser
    public void testGetFreeSlotsByHall() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        List<HallSlot> slots = new ArrayList<>(2);

        slots.add(new HallSlot(1, hall, (short) 1, (short) 1));
        slots.add(new HallSlot(2, hall, (short) 1, (short) 2));

        Integer seatsLeftInHall = (hall.getRows() * hall.getSeatsPerRow()) - slots.size();

        when(hallService.getFreeSlots(1)).thenReturn(seatsLeftInHall);

        mockMvc.perform(get("/halls/1/free-slots"))
            .andExpect(status().isOk())
            .andExpect(content().string(seatsLeftInHall.toString()));
    }

    @Test
    public void testGetFreeSlotsByHallWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/halls/1/free-slots"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteSlot() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        HallSlot slot = new HallSlot(1, hall, (short) 1, (short) 1);

        when(hallSlotService.getById(1)).thenReturn(Optional.of(slot));
        doNothing().when(hallSlotService).delete(slot);

        mockMvc.perform(delete("/slots/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDeleteSlotAsStandardUser() throws Exception
    {
        mockMvc.perform(delete("/slots/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteSlotWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(delete("/slots/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testCreateHallSlot() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        HallSlot slot = new HallSlot(1, hall, (short) 1, (short) 1);

        when(hallService.getById(1)).thenReturn(Optional.of(hall));
        when(hallSlotService.save(any(HallSlot.class))).thenReturn(slot);

        String slotInJson = "{\"seat\": 1, \"row\": 1}";

        mockMvc.perform(post(
            "/halls/1/slots")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_SLOT_PATH + "/" + slot.getId()))
        .andExpect(jsonPath("$.id").value(slot.getId()))
        .andExpect(jsonPath("$.row").value(slot.getRow().toString()))
        .andExpect(jsonPath("$.seat").value(slot.getSeat().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_SLOT_PATH + "/" + slot.getId()))
        .andExpect(jsonPath("$._links.slots_by_hall.href").value(BASE_SLOTS_PATH));
    }

    @Test
    public void testCreateHallSlotWhenNotAuthenticated() throws Exception
    {
        String slotInJson = "{\"seat\": 1, \"row\": 1}";

        mockMvc.perform(post(
            "/halls/1/slots")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateHallSlotAsAdmin() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        HallSlot slot = new HallSlot(1, hall, (short) 1, (short) 1);
        HallSlot updatedSlot = new HallSlot(1, hall, (short) 1, (short) 2);

        when(hallSlotService.getById(1)).thenReturn(Optional.of(slot));
        when(hallSlotService.save(any(HallSlot.class))).thenReturn(updatedSlot);

        String slotInJson = "{\"seat\": 2, \"row\": 1}";

        mockMvc.perform(put(
            "/slots/1")
            .content(slotInJson)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_SLOT_PATH + "/" + updatedSlot.getId()))
        .andExpect(jsonPath("$.id").value(updatedSlot.getId()))
        .andExpect(jsonPath("$.row").value(updatedSlot.getRow().toString()))
        .andExpect(jsonPath("$.seat").value(updatedSlot.getSeat().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_SLOT_PATH + "/" + updatedSlot.getId()))
        .andExpect(jsonPath("$._links.slots_by_hall.href").value(BASE_SLOTS_PATH));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testUpdateHallSlotAsEditor() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);

        HallSlot slot = new HallSlot(1, hall, (short) 1, (short) 1);
        HallSlot updatedSlot = new HallSlot(1, hall, (short) 1, (short) 2);

        when(hallSlotService.getById(1)).thenReturn(Optional.of(slot));
        when(hallSlotService.save(any(HallSlot.class))).thenReturn(updatedSlot);

        String slotInJson = "{\"seat\": 2, \"row\": 1}";

        mockMvc.perform(put(
            "/slots/1")
            .content(slotInJson)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_SLOT_PATH + "/" + updatedSlot.getId()))
        .andExpect(jsonPath("$.id").value(updatedSlot.getId()))
        .andExpect(jsonPath("$.row").value(updatedSlot.getRow().toString()))
        .andExpect(jsonPath("$.seat").value(updatedSlot.getSeat().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_SLOT_PATH + "/" + updatedSlot.getId()))
        .andExpect(jsonPath("$._links.slots_by_hall.href").value(BASE_SLOTS_PATH));
    }

    @Test
    @WithMockUser
    public void testUpdateHallSlotAsStandardUser() throws Exception
    {
        String slotInJson = "{\"seat\": 2, \"row\": 1}";

        mockMvc.perform(put(
            "/slots/1")
            .content(slotInJson)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateHallSlotWhenNotAuthenticated() throws Exception
    {
        String slotInJson = "{\"seat\": 2, \"row\": 1}";

        mockMvc.perform(put(
            "/slots/1")
            .content(slotInJson)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }
}
