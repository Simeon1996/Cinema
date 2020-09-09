package system.cinema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import system.cinema.exception.CinemaEntityNotFoundException;
import system.cinema.model.*;
import system.cinema.model.Role;
import system.cinema.security.AuthDetails;
import system.cinema.service.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
public class DataInitiator implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(DataInitiator.class);

    @Autowired
    private EntitiesConfiguration config;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieTypeService movieTypeService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketTypeService ticketTypeService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private HallTypeService hallTypeService;

    @Autowired
    private HallService hallService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Populating mandatory data");

        roleService.deleteAll();
        userService.deleteAll();
        countryService.deleteAll();
        cityService.deleteAll();
        movieService.deleteAll();
        movieTypeService.deleteAll();
        cinemaService.deleteAll();
        ticketService.deleteAll();
        ticketTypeService.deleteAll();
        hallTypeService.deleteAll();

        // Populate roles
        for (Role.Unit roleUnit : Role.Unit.values()) {
            roleService.save(new Role(roleUnit));
        }

        // Populate Hall Types
        for (HallType.Unit hallTypeUnit : HallType.Unit.values()) {
            hallTypeService.save(new HallType(hallTypeUnit));
        }

        // Populate Countries
        for (Country.Unit countryUnit : Country.Unit.values()) {
            countryService.save(new Country(countryUnit));
        }

        // Populate Cities
        for (City.Unit cityUnit : City.Unit.values()) {
            cityService.save(new City(cityUnit));
        }

        // Populate Movie Types
        for (MovieType.Unit movieTypeUnit : MovieType.Unit.values()) {
            movieTypeService.save(new MovieType(movieTypeUnit));
        }

        // Populate Ticket Types
        for (TicketType.Unit ticketTypeUnit : TicketType.Unit.values()) {
            ticketTypeService.save(new TicketType(ticketTypeUnit));
        }

        // Populate cinemas along with their specified number of halls, types and capacity
        for (Cinema.Unit cinemaUnit : Cinema.Unit.values()) {

            Country country = countryService.getByName(cinemaUnit.getCountry())
                    .orElseThrow(() -> new CinemaEntityNotFoundException("The country was not found."));

            City city = cityService.getByName(cinemaUnit.getCity())
                    .orElseThrow(() -> new CinemaEntityNotFoundException("The city was not found."));

            Cinema cinema = cinemaService.save(new Cinema(cinemaUnit, city, country));

            hallService.saveHalls(cinemaUnit, cinema);
        }

        Role role = roleService.getByName(Role.Unit.ROLE_ADMIN).get();

        // Populate user
        // @TODO The creation of user does not belong here.
        User user = new User("user", "password", role);
        userService.save(user);

        // @TODO A source for movies that will be periodically updated must exist like excel file or a url that contains all movies which will be available
        // for a certain amount of time, for now they will be hardcoded
//        List<Movie> movies = new ArrayList<>();

//            Movie movie1 = new Movie("Dummy-movie-1", Time.valueOf("10:0:0"), Time.valueOf("12:15:0"), Date.valueOf("2020.25.03"), (short) 5, )

//        Movie movie = new Movie(1, "Dummy Movie", Time.valueOf("10:00:00"), Time.valueOf("12:15:00"), Date.valueOf(LocalDate.now()), (short) 5, hallService.getById(1).get(), movieTypeService.getById(1).get());

//        movieService.save(movie);
    }
}
