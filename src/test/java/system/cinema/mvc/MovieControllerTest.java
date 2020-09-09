package system.cinema.mvc;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import system.cinema.exception.ErrorMessageResponse;
import system.cinema.model.*;
import system.cinema.service.HallService;
import system.cinema.service.MovieService;
import system.cinema.service.MovieTypeService;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class MovieControllerTest {

    private static final String BASE_PATH = "http://localhost/movies";
    private static final String BASE_PATH_PER_CINEMA = "http://localhost/cinemas/1/movies";
    private static final String BASE_PATH_PER_CINEMA_TEMPLATED = "http://localhost/cinemas/1/movies{?type}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private HallService hallService;

    @MockBean
    private MovieTypeService movieTypeService;

    private static DateTimeFormatter dateFormatter;

    @BeforeClass
    public static void init()
    {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Test
    @WithMockUser
    public void testGetMoviesByCinema() throws Exception
    {
        List<Movie> movies = new ArrayList<>(2);

        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall1 = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        Hall hall2 = new Hall(2, (short) 2, (short) 7, (short) 7, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType movieType = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();

        Movie movie1 = new Movie(
            1, "Dummy Movie1", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall1, movieType
        );
        Movie movie2 = new Movie(
            2, "Dummy Movie2", Time.valueOf("13:00:00"), Time.valueOf("15:15:00"), Date.valueOf(today), (short) 6, hall2, movieType
        );

        movies.add(movie1);
        movies.add(movie2);

        when(movieService.getAllByCinemaId(cinema.getId())).thenReturn(movies);

        mockMvc.perform(get("/cinemas/1/movies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.movieList[0].id").value(movie1.getId()))
            .andExpect(jsonPath("$._embedded.movieList[0].name").value(movie1.getName()))
            .andExpect(jsonPath("$._embedded.movieList[0].rating").value(movie1.getRating().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0].startTime").value(movie1.getStartTime().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0].endTime").value(movie1.getEndTime().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0].startDate").value(movie1.getStartDate().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0]._links.self.href").value(BASE_PATH + "/" + movie1.getId()))
            .andExpect(jsonPath("$._embedded.movieList[0]._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._embedded.movieList[0]._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie1.getType().getId()))
            .andExpect(jsonPath("$._embedded.movieList[1].id").value(movie2.getId()))
            .andExpect(jsonPath("$._embedded.movieList[1].name").value(movie2.getName()))
            .andExpect(jsonPath("$._embedded.movieList[1].rating").value(movie2.getRating().toString()))
            .andExpect(jsonPath("$._embedded.movieList[1].startTime").value(movie2.getStartTime().toString()))
            .andExpect(jsonPath("$._embedded.movieList[1].endTime").value(movie2.getEndTime().toString()))
            .andExpect(jsonPath("$._embedded.movieList[1].startDate").value(movie2.getStartDate().toString()))
            .andExpect(jsonPath("$._embedded.movieList[1]._links.self.href").value(BASE_PATH + "/" + movie2.getId()))
            .andExpect(jsonPath("$._embedded.movieList[1]._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._embedded.movieList[1]._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie2.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_CINEMA_TEMPLATED));
    }

    @Test
    public void testGetMoviesByCinemaWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/cinemas/1/movies"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetMoviesByCinemaAndType() throws Exception
    {
        List<Movie> movies = new ArrayList<>(1);

        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall1 = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType movieType = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();

        Movie movie1 = new Movie(1, "Dummy Movie1", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall1, movieType);

        movies.add(movie1);

        when(movieService.getAllByCinemaIdAndType(cinema.getId(), movieType.getId())).thenReturn(movies);

        mockMvc.perform(get("/cinemas/1/movies?type=" + movieType.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.movieList[0].id").value(movie1.getId()))
            .andExpect(jsonPath("$._embedded.movieList[0].name").value(movie1.getName()))
            .andExpect(jsonPath("$._embedded.movieList[0].rating").value(movie1.getRating().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0].startTime").value(movie1.getStartTime().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0].endTime").value(movie1.getEndTime().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0].startDate").value(movie1.getStartDate().toString()))
            .andExpect(jsonPath("$._embedded.movieList[0]._links.self.href").value(BASE_PATH + "/" + movie1.getId()))
            .andExpect(jsonPath("$._embedded.movieList[0]._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._embedded.movieList[0]._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie1.getType().getId()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH_PER_CINEMA + "?type=" + movieType.getId()));
    }

    @Test
    @WithMockUser
    public void testGetMovieById() throws Exception
    {
        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType movieType = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate twoDaysFuture = LocalDate.now().plusDays(2);

        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(twoDaysFuture), (short) 5, hall, movieType);

        when(movieService.getById(1)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/movies/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(movie.getId()))
            .andExpect(jsonPath("$.name").value(movie.getName()))
            .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
            .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
            .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
            .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    public void testGetMovieByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/movies/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

//    @Test
//    public void testCreateMovie() throws Exception
//    {
//        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
//        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
//        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
//        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf("2020-03-25"), (short) 5, hall, standard);
//
//        when(hallService.getById(hall.getId())).thenReturn(Optional.of(hall));
//        when(movieTypeService.getById(standard.getId())).thenReturn(Optional.of(standard));
//        when(movieService.save(any(Movie.class))).thenReturn(movie);
//
//        String movieAsJson = "{\"name\": \"Dummy Movie\", \"startTime\": \"10:00:00\", \"endTime\": \"11:15:00\", \"startDate\": \"2020-03-25\", \"rating\": 5}";
//
//        mockMvc.perform(post("/halls/1/movies?type=1")
//            .content(movieAsJson)
//            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//        )
//        .andExpect(status().isCreated())
//        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
//        .andExpect(jsonPath("$.id").value(movie.getId()))
//        .andExpect(jsonPath("$.name").value(movie.getName()))
//        .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
//        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
//        .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
//        .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
//            .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
//    }

    @Test
    @WithMockUser
    public void testCreateMovieWithBiggerStartTimeThanEndTime() throws Exception
    {
        String todayAsString = dateFormatter.format(LocalDate.now());
        String movieAsJson =
            "{\"name\": \"Dummy Movie\", \"startTime\": \"12:00:00\", \"endTime\": \"11:15:00\", \"startDate\": \"" + todayAsString + "\", \"rating\": 5}";

        mockMvc.perform(post("/halls/1/movies?type=1")
                .content(movieAsJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // StartTime cannot be bigger than endTime
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateMovieAsAdmin() throws Exception
    {
        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();
        String todayAsString = dateFormatter.format(today);

        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall, standard);

        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieService.save(any(Movie.class))).thenReturn(movie);

        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"11:15:00\", \"startDate\": \"" + todayAsString +"\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
        .andExpect(jsonPath("$.id").value(movie.getId()))
        .andExpect(jsonPath("$.name").value(movie.getName()))
        .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
        .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
        .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
        .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
        .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
        .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
            .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testUpdateMovieAsEditor() throws Exception
    {
        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();
        String todayAsString = dateFormatter.format(today);

        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall, standard);

        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieService.save(any(Movie.class))).thenReturn(movie);

        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"11:15:00\", \"startDate\": \"" + todayAsString +"\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
        .andExpect(jsonPath("$.id").value(movie.getId()))
        .andExpect(jsonPath("$.name").value(movie.getName()))
        .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
        .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
        .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
        .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
        .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
        .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
            .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    @WithMockUser
    public void testUpdateMovieAsStandardUser() throws Exception
    {
        LocalDate today = LocalDate.now();
        String todayAsString = dateFormatter.format(today);

        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"11:15:00\", \"startDate\": \"" + todayAsString +"\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateMovieWhenNotAuthenticated() throws Exception
    {
        LocalDate today = LocalDate.now();
        String todayAsString = dateFormatter.format(today);

        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"11:15:00\", \"startDate\": \"" + todayAsString +"\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testUpdateMovieEndTimeToEarlierThanStartTime() throws Exception
    {
        String todayAsString = dateFormatter.format(LocalDate.now());
        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"09:15:00\", \"startDate\": \"" + todayAsString +"\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation fails because endTime is earlier than startTime
    }

    @Test
    @WithMockUser
    public void testUpdateMovieDurationToBeBelowMinimumDuration() throws Exception
    {
        String todayAsString = dateFormatter.format(LocalDate.now());

        // The minimum duration is set in movie.minimum.duration.value
        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"10:15:00\", \"startDate\": \"" + todayAsString + "\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation fails because the movie duration is less than 40 minutes
    }

    @Test
    @WithMockUser
    public void testUpdateMovieToNotHaveStartTimeField() throws Exception
    {
        String todayAsString = dateFormatter.format(LocalDate.now());
        String jsonAsString = "{\"name\": \"Dummy Movie Updated\", \"endTime\": \"10:15:00\", \"startDate\": \"" + todayAsString +"\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation fails because startTime field is not set
    }

    @Test
    @WithMockUser
    public void testUpdateMovieToNotHaveEndTimeField() throws Exception
    {
        String todayAsString = dateFormatter.format(LocalDate.now());
        String jsonAsString = "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"startDate\": \"" +  todayAsString + "\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation fails because endTime field is not set
    }

    @Test
    @WithMockUser
    public void testUpdateMovieToNotHaveStartDateField() throws Exception
    {
        String jsonAsString = "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"11:15:00\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation fails because startDate field is not set
    }

    @Test
    @WithMockUser
    public void testUpdateSettingMovieStartDatePast() throws Exception
    {
        String todayAsString = dateFormatter.format(LocalDate.now().minusDays(2));
        String jsonAsString =
            "{\"name\": \"Dummy Movie Updated\", \"startTime\": \"10:00:00\", \"endTime\": \"10:15:00\", \"startDate\": \"" + todayAsString + "\", \"rating\": 5}";

        mockMvc.perform(put("/movies/1")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .content(jsonAsString)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(ErrorMessageResponse.METHOD_ARGUMENT_NOT_VALID.getMessage())); // Validation fails because startDate field is not set
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteMovieAsAdmin() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(LocalDate.now()), (short) 5, hall, standard);

        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        doNothing().when(movieService).delete(movie);

        mockMvc.perform(delete("/movies/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testDeleteMovieAsEditor() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(LocalDate.now()), (short) 5, hall, standard);

        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        doNothing().when(movieService).delete(movie);

        mockMvc.perform(delete("/movies/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testDeleteMovieAsStandardUser() throws Exception
    {
        mockMvc.perform(delete("/movies/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteMovieWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(delete("/movies/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testChangeMovieHallAsAdmin() throws Exception
    {
        HallType standardHallType = new HallType(HallType.Unit.STANDARD);

        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall1 = new Hall(1, (short) 1, (short) 6, (short) 6, standardHallType, cinema);
        Hall hall2 = new Hall(2, (short) 2, (short) 6, (short) 6, standardHallType, cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall1, standard);

        when(hallService.getById(hall2.getId())).thenReturn(Optional.of(hall2));
        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieService.save(any(Movie.class))).thenReturn(movie);

        mockMvc.perform(put("/movies/1/to-hall/2"))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$.id").value(movie.getId()))
            .andExpect(jsonPath("$.name").value(movie.getName()))
            .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
            .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
            .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
            .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testChangeMovieHallAsEditor() throws Exception
    {
        HallType standardHallType = new HallType(HallType.Unit.STANDARD);

        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall1 = new Hall(1, (short) 1, (short) 6, (short) 6, standardHallType, cinema);
        Hall hall2 = new Hall(2, (short) 2, (short) 6, (short) 6, standardHallType, cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall1, standard);

        when(hallService.getById(hall2.getId())).thenReturn(Optional.of(hall2));
        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieService.save(any(Movie.class))).thenReturn(movie);

        mockMvc.perform(put("/movies/1/to-hall/2"))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$.id").value(movie.getId()))
            .andExpect(jsonPath("$.name").value(movie.getName()))
            .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
            .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
            .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
            .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    @WithMockUser
    public void testChangeMovieHallAsStandardUser() throws Exception
    {
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();

        mockMvc.perform(put("/movies/1/to-hall/2"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeMovieHallWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(put("/movies/1/to-hall/2"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testChangeSameMovieHall() throws Exception
    {
        HallType standardHallType = new HallType(HallType.Unit.STANDARD);

        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall1 = new Hall(1, (short) 1, (short) 6, (short) 6, standardHallType, cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall1, standard);

        when(movieService.getById(1)).thenReturn(Optional.of(movie));

        mockMvc.perform(put("/movies/1/to-hall/1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("The movie is already bounded to this hall."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testChangeMovieTypeAsAdmin() throws Exception
    {
        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        MovieType standard3D = new MovieType(2, MovieType.Unit.STANDARD_3D);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall, standard);

        when(movieTypeService.getById(standard3D.getId())).thenReturn(Optional.of(standard3D));
        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieService.save(movie)).thenReturn(movie);

        mockMvc.perform(put("/movies/1/to-type/2"))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$.id").value(movie.getId()))
            .andExpect(jsonPath("$.name").value(movie.getName()))
            .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
            .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
            .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
            .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testChangeMovieTypeAsEditor() throws Exception
    {
        Cinema cinema = new Cinema(1, Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        MovieType standard3D = new MovieType(2, MovieType.Unit.STANDARD_3D);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall, standard);

        when(movieTypeService.getById(standard3D.getId())).thenReturn(Optional.of(standard3D));
        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieService.save(movie)).thenReturn(movie);

        mockMvc.perform(put("/movies/1/to-type/2"))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$.id").value(movie.getId()))
            .andExpect(jsonPath("$.name").value(movie.getName()))
            .andExpect(jsonPath("$.rating").value(movie.getRating().toString()))
            .andExpect(jsonPath("$.startTime").value(movie.getStartTime().toString()))
            .andExpect(jsonPath("$.endTime").value(movie.getEndTime().toString()))
            .andExpect(jsonPath("$.startDate").value(movie.getStartDate().toString()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + movie.getId()))
            .andExpect(jsonPath("$._links.movies_by_cinema.href").value(BASE_PATH_PER_CINEMA_TEMPLATED))
            .andExpect(jsonPath("$._links.movies_by_cinema_and_type.href")
                .value(BASE_PATH_PER_CINEMA + "?type=" + movie.getType().getId()));
    }

    @Test
    @WithMockUser
    public void testChangeMovieTypeAsStandardUser() throws Exception
    {
        mockMvc.perform(put("/movies/1/to-type/2"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testChangeMovieTypeWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(put("/movies/1/to-type/2"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testChangeSameMovieTypeAsAdmin() throws Exception
    {
        Cinema cinema = new Cinema(Cinema.Unit.RING_MALL, new City(City.Unit.SOFIA), new Country(Country.Unit.BULGARIA));
        Hall hall = new Hall(1, (short) 1, (short) 6, (short) 6, new HallType(HallType.Unit.STANDARD), cinema);
        MovieType standard = new MovieType(1, MovieType.Unit.STANDARD_2D);
        LocalDate today = LocalDate.now();
        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(today), (short) 5, hall, standard);

        when(movieService.getById(movie.getId())).thenReturn(Optional.of(movie));

        mockMvc.perform(put("/movies/1/to-type/1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("The movie is already bounded to the provided type.")); // An attempt to set the same movie type was made
    }
}
