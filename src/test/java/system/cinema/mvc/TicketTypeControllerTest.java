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
import system.cinema.model.TicketType;
import system.cinema.service.TicketTypeService;

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
public class TicketTypeControllerTest {

    private static final String BASE_PATH = "http://localhost/ticket-types";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketTypeService ticketTypeService;

    @Test
    @WithMockUser
    public void testGetAllTicketTypes() throws Exception
    {
        List<TicketType> ticketTypeList = new ArrayList<>(4);

        ticketTypeList.add(new TicketType(1, TicketType.Unit.CHILDREN));
        ticketTypeList.add(new TicketType(2, TicketType.Unit.STANDARD));
        ticketTypeList.add(new TicketType(3, TicketType.Unit.ELDERLY));
        ticketTypeList.add(new TicketType(4, TicketType.Unit.STUDENTS));

        when(ticketTypeService.getAll()).thenReturn(ticketTypeList);

        mockMvc.perform(get("/ticket-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.ticketTypeList", hasSize(4)))
            .andExpect(jsonPath("$._embedded.ticketTypeList[0].id").value(1))
            .andExpect(jsonPath("$._embedded.ticketTypeList[0].type").value(TicketType.Unit.CHILDREN.toString()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[0].price").value(TicketType.Unit.CHILDREN.getPrice()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[0]._links.self.href").value(BASE_PATH + "/" + 1))
            .andExpect(jsonPath("$._embedded.ticketTypeList[0]._links.ticket_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.ticketTypeList[1].id").value(2))
            .andExpect(jsonPath("$._embedded.ticketTypeList[1].type").value(TicketType.Unit.STANDARD.toString()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[1].price").value(TicketType.Unit.STANDARD.getPrice()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[1]._links.self.href").value(BASE_PATH + "/" + 2))
            .andExpect(jsonPath("$._embedded.ticketTypeList[1]._links.ticket_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.ticketTypeList[2].id").value(3))
            .andExpect(jsonPath("$._embedded.ticketTypeList[2].type").value(TicketType.Unit.ELDERLY.toString()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[2].price").value(TicketType.Unit.ELDERLY.getPrice()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[2]._links.self.href").value(BASE_PATH + "/" + 3))
            .andExpect(jsonPath("$._embedded.ticketTypeList[2]._links.ticket_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.ticketTypeList[3].id").value(4))
            .andExpect(jsonPath("$._embedded.ticketTypeList[3].type").value(TicketType.Unit.STUDENTS.toString()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[3].price").value(TicketType.Unit.STUDENTS.getPrice()))
            .andExpect(jsonPath("$._embedded.ticketTypeList[3]._links.self.href").value(BASE_PATH + "/" + 4))
            .andExpect(jsonPath("$._embedded.ticketTypeList[3]._links.ticket_types.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllTicketTypesWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/ticket-types"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetTicketTypeById() throws Exception
    {
        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);

        when(ticketTypeService.getById(1)).thenReturn(Optional.of(ticketType));

        mockMvc.perform(get("/ticket-types/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.type").value(TicketType.Unit.STANDARD.toString()))
            .andExpect(jsonPath("$.price").value(TicketType.Unit.STANDARD.getPrice()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + 1))
            .andExpect(jsonPath("$._links.ticket_types.href").value(BASE_PATH));
    }

    @Test
    public void testGetTicketTypeByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/ticket-types/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetUnknownTicketById() throws Exception
    {
        mockMvc.perform(get("/ticket-types/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The ticketType was not found."));
    }
}
