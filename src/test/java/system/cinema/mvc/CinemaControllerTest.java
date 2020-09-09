package system.cinema.mvc;

import static org.hamcrest.Matchers.is;
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
import system.cinema.model.Cinema;
import system.cinema.model.City;
import system.cinema.model.Country;
import system.cinema.service.CinemaService;
import system.cinema.service.HallService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CinemaControllerTest {

    private static final String BASE_PATH = "http://localhost/cinemas";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CinemaService cinemaService;

    @MockBean
    private HallService hallService;

    @Test
    @WithMockUser
    public void testGetAllCinemas() throws Exception
    {
        City city = new City(City.Unit.SOFIA);
        Country country = new Country(Country.Unit.BULGARIA);

        List<Cinema> cinemas = new ArrayList<>(2);

        Cinema ringMall = new Cinema(1, Cinema.Unit.RING_MALL, city, country);
        Cinema bulgariaMall = new Cinema(2, Cinema.Unit.BULGARIA_MALL, city, country);

        cinemas.add(ringMall);
        cinemas.add(bulgariaMall);

        when(cinemaService.getAll()).thenReturn(cinemas);;

        mockMvc.perform(get("/cinemas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.cinemaList", hasSize(2)))
            .andExpect(jsonPath("$._embedded.cinemaList[0].id", is(ringMall.getId())))
            .andExpect(jsonPath("$._embedded.cinemaList[0].name", is(Cinema.Unit.RING_MALL.toString())))
            .andExpect(jsonPath("$._embedded.cinemaList[0].address", is(Cinema.Unit.RING_MALL.getAddress())))
            .andExpect(jsonPath("$._embedded.cinemaList[0]._links.self.href").value(BASE_PATH + "/" + ringMall.getId()))
            .andExpect(jsonPath("$._embedded.cinemaList[0]._links.cinemas.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.cinemaList[1].id", is(bulgariaMall.getId())))
            .andExpect(jsonPath("$._embedded.cinemaList[1].name", is(Cinema.Unit.BULGARIA_MALL.toString())))
            .andExpect(jsonPath("$._embedded.cinemaList[1].address", is(Cinema.Unit.BULGARIA_MALL.getAddress())))
            .andExpect(jsonPath("$._embedded.cinemaList[1]._links.self.href").value(BASE_PATH + "/" + bulgariaMall.getId()))
            .andExpect(jsonPath("$._embedded.cinemaList[1]._links.cinemas.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllCinemasWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/cinemas"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetCinemaById() throws Exception
    {
        City city = new City(City.Unit.SOFIA);
        Country country = new Country(Country.Unit.BULGARIA);

        Cinema ringMall = new Cinema(1, Cinema.Unit.RING_MALL, city, country);

        when(cinemaService.getById(1)).thenReturn(Optional.of(ringMall));

        mockMvc.perform(get("/cinemas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(ringMall.getId())))
            .andExpect(jsonPath("$.name", is(Cinema.Unit.RING_MALL.toString())))
            .andExpect(jsonPath("$.address", is(Cinema.Unit.RING_MALL.getAddress())))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + ringMall.getId()))
            .andExpect(jsonPath("$._links.cinemas.href").value(BASE_PATH));
    }

    @Test
    public void testGetCinemaByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/cinemas/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testNonExistentCinemaById() throws Exception
    {
        mockMvc.perform(get("/cinemas/2"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The cinema was not found."));
    }
}
