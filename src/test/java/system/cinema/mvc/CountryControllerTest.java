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
import system.cinema.model.Country;
import system.cinema.service.CountryService;

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
public class CountryControllerTest {

    private static final String BASE_PATH = "http://localhost/countries";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Test
    @WithMockUser
    public void testGetAllCountries() throws Exception
    {
        List<Country> countries = new ArrayList<>(2);;

        Country bulgaria = new Country(1, Country.Unit.BULGARIA);

        countries.add(bulgaria);

        when(countryService.getAll()).thenReturn(countries);

        mockMvc.perform(get("/countries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.countryList", hasSize(1)))
            .andExpect(jsonPath("$._embedded.countryList[0].id").value(bulgaria.getId()))
            .andExpect(jsonPath("$._embedded.countryList[0].name").value(bulgaria.getName()))
            .andExpect(jsonPath("$._embedded.countryList[0]._links.self.href").value(BASE_PATH + "/" + bulgaria.getId()))
            .andExpect(jsonPath("$._embedded.countryList[0]._links.countries.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllCountriesWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/countries"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetById() throws Exception
    {
        Country bulgaria = new Country(1, Country.Unit.BULGARIA);

        when(countryService.getById(1)).thenReturn(Optional.of(bulgaria));

        mockMvc.perform(get("/countries/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(bulgaria.getId()))
            .andExpect(jsonPath("$.name").value(bulgaria.getName()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + bulgaria.getId()))
            .andExpect(jsonPath("$._links.countries.href").value(BASE_PATH));
    }

    @Test
    public void testGetByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/countries/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetByUnknownId() throws Exception
    {
        mockMvc.perform(get("/countries/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The country was not found."));
    }
}