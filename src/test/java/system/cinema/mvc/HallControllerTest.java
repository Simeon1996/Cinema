package system.cinema.mvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import system.cinema.model.*;
import system.cinema.service.HallService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class HallControllerTest {

    private static final String BASE_PATH = "http://localhost/halls";
    private static final String BASE_PATH_PER_CINEMA = "http://localhost/cinemas/1/halls";
    private static final String BASE_PATH_PER_CINEMA_WITH_TEMPLATED_TYPE = "http://localhost/cinemas/1/halls{?type}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HallService hallService;

    @Test
    @WithMockUser
    public void testGetAllHallsByCinema() throws Exception
    {
        List<Hall> halls = new ArrayList<>(2);

        HallType standard = new HallType(1, HallType.Unit.STANDARD);
        HallType imax = new HallType(2, HallType.Unit.IMAX);

        Cinema ringMall = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));

        Hall hall1 = new Hall(1, (short) 1, (short) 5, (short) 5, standard, ringMall);
        Hall hall2 = new Hall(2, (short) 2, (short) 10, (short) 10, imax, ringMall);

        halls.add(hall1);
        halls.add(hall2);

        when(hallService.getByCinemaId(ringMall.getId())).thenReturn(halls);

        mockMvc.perform(get("/cinemas/1/halls"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.hallList", hasSize(halls.size())))
            .andExpect(jsonPath("$._embedded.hallList[0].id").value(hall1.getId()))
            .andExpect(jsonPath("$._embedded.hallList[0].hallNumber").value(1))
            .andExpect(jsonPath("$._embedded.hallList[0]._links.self.href")
                .value(BASE_PATH + "/" + hall1.getId()))
            .andExpect(jsonPath("$._embedded.hallList[0]._links.halls_by_cinema.href")
                .value(BASE_PATH_PER_CINEMA_WITH_TEMPLATED_TYPE))
            .andExpect(jsonPath("$._embedded.hallList[0]._links.halls_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + hall1.getType().getId()))
            .andExpect(jsonPath("$._embedded.hallList[1].id").value(hall2.getId()))
            .andExpect(jsonPath("$._embedded.hallList[1].hallNumber").value(2))
            .andExpect(jsonPath("$._embedded.hallList[1]._links.self.href")
                .value(BASE_PATH + "/" + hall2.getId()))
            .andExpect(jsonPath("$._embedded.hallList[1]._links.halls_by_cinema.href")
                .value(BASE_PATH_PER_CINEMA_WITH_TEMPLATED_TYPE))
            .andExpect(jsonPath("$._embedded.hallList[1]._links.halls_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + hall2.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_CINEMA_WITH_TEMPLATED_TYPE));
    }

    @Test
    public void testGetAllHallsByCinemaWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/cinemas/1/halls"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetAllHallsByCinemaOfCertainHallType() throws Exception
    {
        List<Hall> imaxHalls = new ArrayList<>(1);

        HallType imax = new HallType(2, HallType.Unit.IMAX);

        Cinema ringMall = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));

        Hall hall2 = new Hall(2, (short) 2, (short) 10, (short) 10, imax, ringMall);

        imaxHalls.add(hall2);

        when(hallService.getByCinemaIdAndType(ringMall.getId(), imax.getId())).thenReturn(imaxHalls);

        mockMvc.perform(get("/cinemas/1/halls?type=2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.hallList", hasSize(1)))
            .andExpect(jsonPath("$._embedded.hallList[0].id", is(hall2.getId())))
            .andExpect(jsonPath("$._embedded.hallList[0].hallNumber").value(2))
            .andExpect(jsonPath("$._embedded.hallList[0]._links.self.href").value(BASE_PATH + "/" + hall2.getId()))
            .andExpect(jsonPath("$._embedded.hallList[0]._links.halls_by_cinema.href")
                .value(BASE_PATH_PER_CINEMA_WITH_TEMPLATED_TYPE))
            .andExpect(jsonPath("$._embedded.hallList[0]._links.halls_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + hall2.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_CINEMA + "?type=" + hall2.getId()));
    }

    @Test
    @WithMockUser
    public void testGetHallById() throws Exception
    {
        Cinema ringMall = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        HallType standard = new HallType(1, HallType.Unit.STANDARD);
        Hall hall = new Hall(1, (short) 1, (short) 10, (short) 10, standard, ringMall);

        when(hallService.getById(hall.getId())).thenReturn(Optional.of(hall));

        mockMvc.perform(get("/halls/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(hall.getId()))
            .andExpect(jsonPath("$.hallNumber").value(hall.getHallNumber().toString()))
            .andExpect(jsonPath("$.rows").value(hall.getRows().toString()))
            .andExpect(jsonPath("$.seatsPerRow").value(hall.getSeatsPerRow().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + hall.getId()))
            .andExpect(jsonPath("$._links.halls_by_cinema.href")
                .value(BASE_PATH_PER_CINEMA_WITH_TEMPLATED_TYPE))
            .andExpect(jsonPath("$._links.halls_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + hall.getType().getId()));
    }

    @Test
    public void testGetHallByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/halls/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetHallByUnknownId() throws Exception
    {
        mockMvc.perform(get("/halls/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The hall was not found."));
    }
}
