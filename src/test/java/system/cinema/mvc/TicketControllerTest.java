package system.cinema.mvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import system.cinema.exception.ErrorMessageResponse;
import system.cinema.model.*;
import system.cinema.service.MovieService;
import system.cinema.service.TicketService;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
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
public class TicketControllerTest {

    private static final String BASE_PATH = "http://localhost/tickets";
    private static final String BASE_PATH_PER_MOVIE = "http://localhost/movies/1/tickets";
    private static final String BASE_PATH_PER_MOVIE_TEMPLATED = "http://localhost/movies/1/tickets{?type}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private MovieService movieService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllByMovieAsAdmin() throws Exception
    {
        List<Ticket> tickets = new ArrayList<>();

        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);
        Ticket ticket1 = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie
        );

        Ticket ticket2 = new Ticket(
            2,
            "Dummy Dummiev",
            "dummymail1@abv.bg",
            "+359882311221",
            (short) 1,
            (short) 1,
            (short) 20,
            ticketType,
            movie
        );

        tickets.add(ticket1);
        tickets.add(ticket2);

        when(ticketService.getAllByMovieId(movie.getId())).thenReturn(tickets);

        mockMvc.perform(get("/movies/1/tickets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.ticketList", hasSize(2)))
            .andExpect(jsonPath("$._embedded.ticketList[0].id").value(ticket1.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[0].belongsTo").value(ticket1.getBelongsTo()))
            .andExpect(jsonPath("$._embedded.ticketList[0].email").value(ticket1.getEmail()))
            .andExpect(jsonPath("$._embedded.ticketList[0].phone").value(ticket1.getPhone()))
            .andExpect(jsonPath("$._embedded.ticketList[0].row").value(ticket1.getRow().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0].seat").value(ticket1.getSeat().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0].age").value(ticket1.getAge().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.self.href").value(BASE_PATH + "/" + ticket1.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.tickets_by_movie.href")
                .value(BASE_PATH_PER_MOVIE_TEMPLATED))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.tickets_by_movie_and_type.href")
                .value(BASE_PATH_PER_MOVIE + "?type=" + ticket1.getType().getId()))
            .andExpect(jsonPath("$._embedded.ticketList[1].id").value(ticket2.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[1].belongsTo").value(ticket2.getBelongsTo()))
            .andExpect(jsonPath("$._embedded.ticketList[1].email").value(ticket2.getEmail()))
            .andExpect(jsonPath("$._embedded.ticketList[1].phone").value(ticket2.getPhone()))
            .andExpect(jsonPath("$._embedded.ticketList[1].row").value(ticket2.getRow().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[1].seat").value(ticket2.getSeat().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[1].age").value(ticket2.getAge().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[1]._links.self.href").value(BASE_PATH + "/" + ticket2.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[1]._links.tickets_by_movie.href")
                .value(BASE_PATH_PER_MOVIE_TEMPLATED))
            .andExpect(jsonPath("$._embedded.ticketList[1]._links.tickets_by_movie_and_type.href")
                .value(BASE_PATH_PER_MOVIE + "?type=" + ticket2.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_MOVIE_TEMPLATED));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testGetAllByMovieAsEditor() throws Exception
    {
        List<Ticket> tickets = new ArrayList<>();

        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);
        Ticket ticket1 = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie
        );

        Ticket ticket2 = new Ticket(
            2,
            "Dummy Dummiev",
            "dummymail1@abv.bg",
            "+359882311221",
            (short) 1,
            (short) 1,
            (short) 20,
            ticketType,
            movie
        );

        tickets.add(ticket1);
        tickets.add(ticket2);

        when(ticketService.getAllByMovieId(movie.getId())).thenReturn(tickets);

        mockMvc.perform(get("/movies/1/tickets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.ticketList", hasSize(2)))
            .andExpect(jsonPath("$._embedded.ticketList[0].id").value(ticket1.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[0].belongsTo").value(ticket1.getBelongsTo()))
            .andExpect(jsonPath("$._embedded.ticketList[0].email").value(ticket1.getEmail()))
            .andExpect(jsonPath("$._embedded.ticketList[0].phone").value(ticket1.getPhone()))
            .andExpect(jsonPath("$._embedded.ticketList[0].row").value(ticket1.getRow().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0].seat").value(ticket1.getSeat().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0].age").value(ticket1.getAge().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.self.href").value(BASE_PATH + "/" + ticket1.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.tickets_by_movie.href")
                .value(BASE_PATH_PER_MOVIE_TEMPLATED))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.tickets_by_movie_and_type.href")
                .value(BASE_PATH_PER_MOVIE + "?type=" + ticket1.getType().getId()))
            .andExpect(jsonPath("$._embedded.ticketList[1].id").value(ticket2.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[1].belongsTo").value(ticket2.getBelongsTo()))
            .andExpect(jsonPath("$._embedded.ticketList[1].email").value(ticket2.getEmail()))
            .andExpect(jsonPath("$._embedded.ticketList[1].phone").value(ticket2.getPhone()))
            .andExpect(jsonPath("$._embedded.ticketList[1].row").value(ticket2.getRow().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[1].seat").value(ticket2.getSeat().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[1].age").value(ticket2.getAge().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[1]._links.self.href").value(BASE_PATH + "/" + ticket2.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[1]._links.tickets_by_movie.href")
                .value(BASE_PATH_PER_MOVIE_TEMPLATED))
            .andExpect(jsonPath("$._embedded.ticketList[1]._links.tickets_by_movie_and_type.href")
                .value(BASE_PATH_PER_MOVIE + "?type=" + ticket2.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_MOVIE_TEMPLATED));
    }

    @Test
    @WithMockUser
    public void testGetAllByMovieAsStandardUser() throws Exception
    {
        mockMvc.perform(get("/movies/1/tickets"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllByMovieAndType() throws Exception
    {
        List<Ticket> standardTickets = new ArrayList<>();

        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType standard = new TicketType(1, TicketType.Unit.STANDARD);
        TicketType student = new TicketType(1, TicketType.Unit.STUDENTS);
        Ticket ticket1 = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            standard,
            movie);

        standardTickets.add(ticket1);

        when(ticketService.getAllByMovieIdAndTypeId(movie.getId(), standard.getId())).thenReturn(standardTickets);

        mockMvc.perform(get("/movies/1/tickets?type=" + standard.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.ticketList", hasSize(1)))
            .andExpect(jsonPath("$._embedded.ticketList[0].id").value(ticket1.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[0].belongsTo").value(ticket1.getBelongsTo()))
            .andExpect(jsonPath("$._embedded.ticketList[0].email").value(ticket1.getEmail()))
            .andExpect(jsonPath("$._embedded.ticketList[0].phone").value(ticket1.getPhone()))
            .andExpect(jsonPath("$._embedded.ticketList[0].row").value(ticket1.getRow().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0].seat").value(ticket1.getSeat().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0].age").value(ticket1.getAge().toString()))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.self.href").value(BASE_PATH + "/" + ticket1.getId()))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.tickets_by_movie.href")
                .value(BASE_PATH_PER_MOVIE_TEMPLATED))
            .andExpect(jsonPath("$._embedded.ticketList[0]._links.tickets_by_movie_and_type.href")
                .value(BASE_PATH_PER_MOVIE + "?type=" + ticket1.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_MOVIE + "?type=" + standard.getId()));
    }

    @Test
    @WithMockUser
    public void testGetTicketById() throws Exception
    {
        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);
        Ticket ticket = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie);

        when(ticketService.getById(1)).thenReturn(Optional.of(ticket));

        mockMvc.perform(get("/tickets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ticket.getId()))
            .andExpect(jsonPath("$.belongsTo").value(ticket.getBelongsTo()))
            .andExpect(jsonPath("$.email").value(ticket.getEmail()))
            .andExpect(jsonPath("$.phone").value(ticket.getPhone()))
            .andExpect(jsonPath("$.row").value(ticket.getRow().toString()))
            .andExpect(jsonPath("$.seat").value(ticket.getSeat().toString()))
            .andExpect(jsonPath("$.age").value(ticket.getAge().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + ticket.getId()))
            .andExpect(jsonPath("$._links.tickets_by_movie.href").value(BASE_PATH_PER_MOVIE_TEMPLATED))
            .andExpect(jsonPath("$._links.tickets_by_movie_and_type.href")
                .value(BASE_PATH_PER_MOVIE + "?type=" + ticket.getType().getId()));
    }

    // @TODO Matter of discussion if the user should be able to access its' own ticket or not
    @Test
    public void testGetTicketByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/tickets/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteTicketAsAdmin() throws Exception
    {
        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(TicketType.Unit.STANDARD);
        Ticket ticket = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie);

        when(ticketService.getById(1)).thenReturn(Optional.of(ticket));
        doNothing().when(ticketService).delete(ticket);

        mockMvc.perform(delete("/tickets/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testDeleteTicketAsEditor() throws Exception
    {
        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(TicketType.Unit.STANDARD);
        Ticket ticket = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie);

        when(ticketService.getById(1)).thenReturn(Optional.of(ticket));
        doNothing().when(ticketService).delete(ticket);

        mockMvc.perform(delete("/tickets/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDeleteTicketAsStandardUser() throws Exception
    {
        mockMvc.perform(delete("/tickets/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteTicketWhenNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/tickets/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testCreateTicket() throws Exception
    {
        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);
        Ticket ticket = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie);

        when(movieService.getById(1)).thenReturn(Optional.of(movie));
        when(ticketService.save(any(Ticket.class))).thenReturn(ticket);

        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanov\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313221\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(post("/movies/1/tickets")
            .content(jsonAsString)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + ticket.getId()))
        .andExpect(jsonPath("$.id").value(ticket.getId()))
        .andExpect(jsonPath("$.belongsTo").value(ticket.getBelongsTo()))
        .andExpect(jsonPath("$.email").value(ticket.getEmail()))
        .andExpect(jsonPath("$.phone").value(ticket.getPhone()))
        .andExpect(jsonPath("$.row").value(ticket.getRow().toString()))
        .andExpect(jsonPath("$.seat").value(ticket.getSeat().toString()))
        .andExpect(jsonPath("$.age").value(ticket.getAge().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + ticket.getId()))
        .andExpect(jsonPath("$._links.tickets_by_movie.href").value(BASE_PATH_PER_MOVIE_TEMPLATED))
        .andExpect(jsonPath("$._links.tickets_by_movie_and_type.href")
            .value(BASE_PATH_PER_MOVIE + "?type=" + ticket.getType().getId()));
    }

    @Test
    public void testCreateTicketWhenNotAuthenticated() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanov\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313221\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(post("/movies/1/tickets")
            .content(jsonAsString)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testCreateTicketWithInvalidPhoneNumber() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanov\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"4411231421\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(post("/movies/1/tickets")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation for phone number fails
    }

    @Test
    @WithMockUser
    public void testCreateTicketWithInvalidContactName() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313221\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(post("/movies/1/tickets")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation for contact name fails, must contains at least first and last name
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateTicketAsAdmin() throws Exception
    {
        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);
        Ticket ticket = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie);

        when(ticketService.getById(1)).thenReturn(Optional.of(ticket));
        when(ticketService.save(ticket)).thenReturn(ticket);

        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanovich\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
                .content(jsonAsString)
                .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + ticket.getId()))
        .andExpect(jsonPath("$.id").value(ticket.getId()))
        .andExpect(jsonPath("$.belongsTo").value(ticket.getBelongsTo()))
        .andExpect(jsonPath("$.email").value(ticket.getEmail()))
        .andExpect(jsonPath("$.phone").value(ticket.getPhone()))
        .andExpect(jsonPath("$.row").value(ticket.getRow().toString()))
        .andExpect(jsonPath("$.seat").value(ticket.getSeat().toString()))
        .andExpect(jsonPath("$.age").value(ticket.getAge().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + ticket.getId()))
        .andExpect(jsonPath("$._links.tickets_by_movie.href").value(BASE_PATH_PER_MOVIE_TEMPLATED))
        .andExpect(jsonPath("$._links.tickets_by_movie_and_type.href")
            .value(BASE_PATH_PER_MOVIE + "?type=" + ticket.getType().getId()));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testUpdateTicketAsEditor() throws Exception
    {
        Cinema cinema  = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1,
            "Dummy Movie",
            Time.valueOf("10:00:00"),
            Time.valueOf("12:15:00"),
            Date.valueOf(today),
            (short) 1,
            hall,
            new MovieType(MovieType.Unit.STANDARD_2D)
        );

        TicketType ticketType = new TicketType(1, TicketType.Unit.STANDARD);
        Ticket ticket = new Ticket(
            1,
            "Simeon Ivanov",
            "dummymail@abv.bg",
            "+359882313221",
            (short) 1,
            (short) 2,
            (short) 20,
            ticketType,
            movie);

        when(ticketService.getById(1)).thenReturn(Optional.of(ticket));
        when(ticketService.save(ticket)).thenReturn(ticket);

        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanovich\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + ticket.getId()))
        .andExpect(jsonPath("$.id").value(ticket.getId()))
        .andExpect(jsonPath("$.belongsTo").value(ticket.getBelongsTo()))
        .andExpect(jsonPath("$.email").value(ticket.getEmail()))
        .andExpect(jsonPath("$.phone").value(ticket.getPhone()))
        .andExpect(jsonPath("$.row").value(ticket.getRow().toString()))
        .andExpect(jsonPath("$.seat").value(ticket.getSeat().toString()))
        .andExpect(jsonPath("$.age").value(ticket.getAge().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + ticket.getId()))
        .andExpect(jsonPath("$._links.tickets_by_movie.href").value(BASE_PATH_PER_MOVIE_TEMPLATED))
        .andExpect(jsonPath("$._links.tickets_by_movie_and_type.href")
            .value(BASE_PATH_PER_MOVIE + "?type=" + ticket.getType().getId()));
    }

    @Test
    @WithMockUser
    public void testUpdateTicketAsStandardUser() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanovich\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Forbidden"));
    }

    @Test
    public void testUpdateTicketWhenNotAuthenticated() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanovich\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithEmptyBelongsToField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The belongsTo field cannot be empty
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithoutBelongsToField() throws Exception
    {
        String jsonAsString =
            "{\"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The belongsTo field is mandatory, so an exception will be thrown
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithEmptyEmailField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanov\", \"email\": \"\"," +
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The email field cannot be empty exception
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithoutEmailField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Simeon Ivanov\", "+
            " \"phone\": \"+359882313222\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The email field is mandatory so an exception is thrown
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithoutPhoneField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Dummmy One\", \"email\": \"dummymail@abv.bg\"," +
            " \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The phone field is mandatory so an exception is thrown
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithEmptyPhoneField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Dummmy One\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"\", \"row\": 1, \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The phone field cannot be empty exception
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithoutRowField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Dummmy One\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+3592313221\", \"seat\": 2, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The row field is mandatory, so an exception is thrown
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithoutSeatField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Dummmy One\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+3592313221\", \"row\": 1, \"age\": 20}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The seat field is mandatory, so an exception is thrown
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithoutAgeField() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Dummmy One\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+3592313221\", \"row\": 1, \"seat\": 2}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The age field is mandatory, so an exception is thrown
    }

    @Test
    @WithMockUser
    public void testCreationOfTicketWithInvalidAge() throws Exception
    {
        String jsonAsString =
            "{\"belongsTo\": \"Dummmy One\", \"email\": \"dummymail@abv.bg\"," +
            " \"phone\": \"+3592313221\", \"row\": 1, \"seat\": 2, \"age\": 12}";

        mockMvc.perform(put("/tickets/1")
            .content(jsonAsString)
            .header("Content-type", MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // The age must be bigger or equal to 16, so an exception is thrown
    }
}
