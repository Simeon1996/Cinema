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
import system.cinema.service.HallTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class HallTypeControllerTest {

    private static final String BASE_PATH = "http://localhost/hall-types";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HallTypeService hallTypeService;

    @Test
    @WithMockUser
    public void testGetAllHallTypes() throws Exception
    {
        List<HallType> hallTypeList = new ArrayList<>(2);

        HallType standard = new HallType( 1, HallType.Unit.STANDARD);
        HallType imax = new HallType(2, HallType.Unit.IMAX);

        hallTypeList.add(standard);
        hallTypeList.add(imax);

        when(hallTypeService.getAll()).thenReturn(hallTypeList);

        mockMvc.perform(get("/hall-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.hallTypeList", hasSize(2)))
            .andExpect(jsonPath("$._embedded.hallTypeList[0].id").value(standard.getId()))
            .andExpect(jsonPath("$._embedded.hallTypeList[0].type").value(standard.getType()))
            .andExpect(jsonPath("$._embedded.hallTypeList[0]._links.self.href").value(BASE_PATH + "/" + standard.getId()))
            .andExpect(jsonPath("$._embedded.hallTypeList[0]._links.hall_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.hallTypeList[1].id").value(imax.getId()))
            .andExpect(jsonPath("$._embedded.hallTypeList[1].type").value(imax.getType()))
            .andExpect(jsonPath("$._embedded.hallTypeList[1]._links.self.href").value(BASE_PATH + "/" + imax.getId()))
            .andExpect(jsonPath("$._embedded.hallTypeList[1]._links.hall_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllHallTypesWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/hall-types"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetHallTypeById() throws Exception
    {
        HallType standard = new HallType(1, HallType.Unit.STANDARD);

        when(hallTypeService.getById(standard.getId())).thenReturn(Optional.of(standard));

        mockMvc.perform(get("/hall-types/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(standard.getId()))
            .andExpect(jsonPath("$.type").value(standard.getType()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + standard.getId()))
            .andExpect(jsonPath("$._links.hall_types.href").value(BASE_PATH));
    }

    @Test
    public void testGetHallTypeByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/hall-types/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetUnknownHallTypeById() throws Exception
    {
        mockMvc.perform(get("/hall-types/2"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The hallType was not found."));
    }
}
