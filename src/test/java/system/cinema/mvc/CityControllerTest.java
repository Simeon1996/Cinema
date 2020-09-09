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
import system.cinema.model.City;
import system.cinema.service.CityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CityControllerTest {

    private static final String BASE_PATH = "http://localhost/cities";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityService cityService;

    @Test
    @WithMockUser
    public void testGetAllCities() throws Exception
    {
        List<City> cities = new ArrayList<>(2);;

        City sofia = new City(1, City.Unit.SOFIA);
        City plovdiv = new City(2, City.Unit.PLOVDIV);

        cities.add(sofia);
        cities.add(plovdiv);

        when(cityService.getAll()).thenReturn(cities);

        mockMvc.perform(get("/cities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.cityList", hasSize(2)))
            .andExpect(jsonPath("$._embedded.cityList[0].id").value(sofia.getId()))
            .andExpect(jsonPath("$._embedded.cityList[0].name").value(sofia.getName()))
            .andExpect(jsonPath("$._embedded.cityList[0]._links.self.href").value(BASE_PATH + "/" + sofia.getId()))
            .andExpect(jsonPath("$._embedded.cityList[0]._links.cities.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.cityList[1].id").value(plovdiv.getId()))
            .andExpect(jsonPath("$._embedded.cityList[1].name").value(plovdiv.getName()))
            .andExpect(jsonPath("$._embedded.cityList[1]._links.self.href").value(BASE_PATH + "/" + plovdiv.getId()))
            .andExpect(jsonPath("$._embedded.cityList[1]._links.cities.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllCitiesWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/cities"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetById() throws Exception
    {
        City sofia = new City(1, City.Unit.SOFIA);

        when(cityService.getById(sofia.getId())).thenReturn(Optional.of(sofia));

        mockMvc.perform(get("/cities/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(sofia.getId()))
            .andExpect(jsonPath("$.name").value(sofia.getName()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + sofia.getId()))
            .andExpect(jsonPath("$._links.cities.href").value(BASE_PATH));
    }

    @Test
    public void testGetByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/cities/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetOneByUnknownId() throws Exception
    {
        mockMvc.perform(get("/cities/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The city was not found."));
    }
}