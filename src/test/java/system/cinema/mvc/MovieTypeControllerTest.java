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
import system.cinema.model.MovieType;
import system.cinema.service.MovieTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MovieTypeControllerTest {

    private static final String BASE_PATH = "http://localhost/movie-types";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieTypeService movieTypeService;

    @Test
    @WithMockUser
    public void testGetAllMovieTypes() throws Exception
    {
        List<MovieType> movieTypeList = new ArrayList<>(4);

        MovieType standard2d = new MovieType(1, MovieType.Unit.STANDARD_2D);
        MovieType standard3d = new MovieType(2, MovieType.Unit.STANDARD_3D);
        MovieType imax2d = new MovieType(3, MovieType.Unit.IMAX_2D);
        MovieType imax3d = new MovieType(4, MovieType.Unit.IMAX_3D);

        movieTypeList.add(standard2d);
        movieTypeList.add(standard3d);
        movieTypeList.add(imax2d);
        movieTypeList.add(imax3d);

        when(movieTypeService.getAll()).thenReturn(movieTypeList);

        mockMvc.perform(get("/movie-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.movieTypeList", hasSize(4)))
            .andExpect(jsonPath("$._embedded.movieTypeList[0].id").value(standard2d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[0].type", is(standard2d.getType())))
            .andExpect(jsonPath("$._embedded.movieTypeList[0]._links.self.href").value(BASE_PATH + "/" + standard2d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[0]._links.movie_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.movieTypeList[1].id").value(standard3d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[1].type", is(standard3d.getType())))
            .andExpect(jsonPath("$._embedded.movieTypeList[1]._links.self.href").value(BASE_PATH + "/" + standard3d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[1]._links.movie_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.movieTypeList[2].id").value(imax2d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[2].type", is(imax2d.getType())))
            .andExpect(jsonPath("$._embedded.movieTypeList[2]._links.self.href").value(BASE_PATH + "/" + imax2d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[2]._links.movie_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.movieTypeList[3].id").value(imax3d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[3].type", is(imax3d.getType())))
            .andExpect(jsonPath("$._embedded.movieTypeList[3]._links.self.href").value(BASE_PATH + "/" + imax3d.getId()))
            .andExpect(jsonPath("$._embedded.movieTypeList[3]._links.movie_types.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllMovieTypesWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/movie-types"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetMovieTypeById() throws Exception
    {
        MovieType imax2d = new MovieType(1, MovieType.Unit.IMAX_2D);

        when(movieTypeService.getById(imax2d.getId())).thenReturn(Optional.of(imax2d));

        mockMvc.perform(get("/movie-types/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(imax2d.getId()))
            .andExpect(jsonPath("$.type").value(imax2d.getType()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + imax2d.getId()))
            .andExpect(jsonPath("$._links.movie_types.href").value(BASE_PATH));
    }

    @Test
    public void testGetMovieTypeByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/movie-types/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetUnknownMovieTypeById() throws Exception
    {
        mockMvc.perform(get("/movie-types/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The movieType was not found."));
    }
}
