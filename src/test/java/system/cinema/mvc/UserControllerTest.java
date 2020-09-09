package system.cinema.mvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import system.cinema.model.*;
import system.cinema.service.CinemaService;
import system.cinema.service.RoleService;
import system.cinema.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    private final static String BASE_PATH = "http://localhost/users";

    @MockBean
    private UserService userService;

    @MockBean
    private CinemaService cinemaService;

    @MockBean
    private RoleService roleService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllAsAdmin() throws Exception
    {
        List<User> users = new ArrayList<>(3);

        City city = new City(City.Unit.SOFIA);
        Country country = new Country(Country.Unit.BULGARIA);

        Role roleUser = new Role(1, Role.Unit.ROLE_USER);
        Role roleAdmin = new Role(2, Role.Unit.ROLE_ADMIN);

        User user1 = new User(1,"dummy-user", "dummy-password", roleUser);
        User user2 = new User(2, "dummy-user-2", "dummy-password", roleUser);
        User user3 = new User(3,"dummy-user-admin", "dummy-password", roleAdmin);

        users.add(user1);
        users.add(user2);
        users.add(user3);

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.userList", hasSize(3)))
            .andExpect(jsonPath("$._embedded.userList[0].id").value(user1.getId()))
            .andExpect(jsonPath("$._embedded.userList[0].username").value(user1.getUsername()))
            .andExpect(jsonPath("$._embedded.userList[0].password").doesNotExist())
            .andExpect(jsonPath("$._embedded.userList[0].role.id").value(user1.getRole().getId()))
            .andExpect(jsonPath("$._embedded.userList[0].role.name").value(user1.getRole().getName()))
            .andExpect(jsonPath("$._embedded.userList[0]._links.self.href").value(BASE_PATH + "/" + user1.getId()))
            .andExpect(jsonPath("$._embedded.userList[0]._links.users.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.userList[1].id").value(user2.getId()))
            .andExpect(jsonPath("$._embedded.userList[1].username").value(user2.getUsername()))
            .andExpect(jsonPath("$._embedded.userList[1].password").doesNotExist())
            .andExpect(jsonPath("$._embedded.userList[1].role.id").value(user2.getRole().getId()))
            .andExpect(jsonPath("$._embedded.userList[1].role.name").value(user2.getRole().getName()))
            .andExpect(jsonPath("$._embedded.userList[1]._links.self.href").value(BASE_PATH + "/" + user2.getId()))
            .andExpect(jsonPath("$._embedded.userList[1]._links.users.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.userList[2].id").value(user3.getId()))
            .andExpect(jsonPath("$._embedded.userList[2].username").value(user3.getUsername()))
            .andExpect(jsonPath("$._embedded.userList[2].password").doesNotExist())
            .andExpect(jsonPath("$._embedded.userList[2].role.id").value(user3.getRole().getId()))
            .andExpect(jsonPath("$._embedded.userList[2].role.name").value(user3.getRole().getName()))
            .andExpect(jsonPath("$._embedded.userList[2]._links.self.href").value(BASE_PATH + "/" + user3.getId()))
            .andExpect(jsonPath("$._embedded.userList[2]._links.users.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testGetAllAsEditor() throws Exception
    {
        mockMvc.perform(get("/users"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testGetAllAsStandardUser() throws Exception
    {
        mockMvc.perform(get("/users"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAllWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/users"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetByIdAsAdmin() throws Exception
    {
        Role roleUser = new Role(1, Role.Unit.ROLE_USER);

        User user1 = new User(1,"dummy-user", "dummy-password", roleUser);

        when(userService.getById(1)).thenReturn(Optional.of(user1));

        mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(user1.getId()))
            .andExpect(jsonPath("$.username").value(user1.getUsername()))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.role.id").value(user1.getRole().getId()))
            .andExpect(jsonPath("$.role.name").value(user1.getRole().getName()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + user1.getId()))
            .andExpect(jsonPath("$._links.users.href").value(BASE_PATH));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testGetByIdAsEditor() throws Exception
    {
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testGetByIdAsStandardUser() throws Exception
    {
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testGetByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/users/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateUserAsAdmin() throws Exception
    {
        Role role = new Role(1, Role.Unit.ROLE_ADMIN);

        User user = new User(1, "dummy-user", "dummy-password", role);

        when(roleService.getById(role.getId())).thenReturn(Optional.of(role));
        when(userService.save(any(User.class))).thenReturn(user);

        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"dummy-password\"}";

        mockMvc.perform(post(
            "/users?roleId=1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + user.getId()))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.role.id").value(user.getRole().getId()))
        .andExpect(jsonPath("$.role.name").value(user.getRole().getName()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + user.getId()))
        .andExpect(jsonPath("$._links.users.href").value(BASE_PATH));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testCreateUserAsEditor() throws Exception
    {
        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"dummy-password\"}";

        mockMvc.perform(post(
            "/users?roleId=1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testCreateUserAsStandardUser() throws Exception
    {
        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"dummy-password\"}";

        mockMvc.perform(post(
            "/users?roleId=1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateUserWhenNotAuthenticated() throws Exception
    {
        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"dummy-password\"}";

        mockMvc.perform(post(
            "/users?roleId=1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateUserAsAdmin() throws Exception
    {
        Role role = new Role(1, Role.Unit.ROLE_ADMIN);

        User user = new User(1, "dummy-user", "dummy-password", role);

        when(userService.getById(user.getId())).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenReturn(user);

        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"new-dummy-password\"}";

        mockMvc.perform(put(
            "/users/" + user.getId())
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + user.getId()))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.role.id").value(user.getRole().getId()))
        .andExpect(jsonPath("$.role.name").value(user.getRole().getName()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + user.getId()))
        .andExpect(jsonPath("$._links.users.href").value(BASE_PATH));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testUpdateUserAsEditor() throws Exception
    {
        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"new-dummy-password\"}";

        mockMvc.perform(put(
            "/users/1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testUpdateUserAsStandardUser() throws Exception
    {
        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"new-dummy-password\"}";

        mockMvc.perform(put(
            "/users/1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateUserWhenNotAuthenticated() throws Exception
    {
        String slotInJson = "{\"username\": \"dummy-user\", \"password\": \"new-dummy-password\"}";

        mockMvc.perform(put(
            "/users/1")
            .content(slotInJson)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateUserRoleAsAdmin() throws Exception
    {
        Role role = new Role(1, Role.Unit.ROLE_ADMIN);
        Role newRole = new Role(2, Role.Unit.ROLE_EDITOR);

        User user = new User(1, "dummy-user", "dummy-password", role);

        when(userService.getById(user.getId())).thenReturn(Optional.of(user));
        when(roleService.getById(newRole.getId())).thenReturn(Optional.of(newRole));
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(put(
            "/users/" + user.getId() + "/to-role/" + newRole.getId())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, BASE_PATH + "/" + user.getId()))
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.username").value(user.getUsername()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.role.id").value(newRole.getId()))
        .andExpect(jsonPath("$.role.name").value(newRole.getName()))
        .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + user.getId()))
        .andExpect(jsonPath("$._links.users.href").value(BASE_PATH));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateUserSameRoleAsAdmin() throws Exception
    {
        Role role = new Role(1, Role.Unit.ROLE_ADMIN);
        User user = new User(1, "dummy-user", "dummy-password", role);

        when(userService.getById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(put(
            "/users/" + user.getId() + "/to-role/" + role.getId())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string("The user is already bounded to this role."));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testUpdateUserRoleAsEditor() throws Exception
    {
        mockMvc.perform(put(
            "/users/1/to-role/2")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testUpdateUserRoleAsStandardUser() throws Exception
    {
        mockMvc.perform(put(
            "/users/1/to-role/2")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateUserRoleWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(put(
            "/users/1/to-role/2")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isForbidden())
        .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteUserAsAdmin() throws Exception
    {
        Role role = new Role(1, Role.Unit.ROLE_ADMIN);

        User user = new User(1, "dummy-user", "dummy-password", role);

        when(userService.getById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userService).delete(user);

        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "editor", roles = {"EDITOR"})
    public void testDeleteUserAsEditor() throws Exception
    {
        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testDeleteUserAsStandardUser() throws Exception
    {
        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteUserWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }
}
