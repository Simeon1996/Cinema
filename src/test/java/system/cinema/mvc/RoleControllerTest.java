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
import system.cinema.model.Role;
import system.cinema.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RoleControllerTest {

    private final static String BASE_PATH = "http://localhost/roles";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser
    public void testGetAllRoles() throws Exception
    {
        List<Role> roles = new ArrayList<>();

        Role admin = new Role(1, Role.Unit.ROLE_ADMIN);
        Role editor = new Role(2, Role.Unit.ROLE_EDITOR);
        Role user = new Role(3, Role.Unit.ROLE_USER);

        roles.add(admin);
        roles.add(editor);
        roles.add(user);

        when(roleService.getAll()).thenReturn(roles);

        mockMvc.perform(get("/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.roleList", hasSize(3)))
            .andExpect(jsonPath("$._embedded.roleList[0].id").value(admin.getId()))
            .andExpect(jsonPath("$._embedded.roleList[0].name").value(admin.getName()))
            .andExpect(jsonPath("$._embedded.roleList[0]._links.self.href").value(BASE_PATH + "/" + admin.getId()))
            .andExpect(jsonPath("$._embedded.roleList[0]._links.roles.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.roleList[1].id").value(editor.getId()))
            .andExpect(jsonPath("$._embedded.roleList[1].name").value(editor.getName()))
            .andExpect(jsonPath("$._embedded.roleList[1]._links.self.href").value(BASE_PATH + "/" + editor.getId()))
            .andExpect(jsonPath("$._embedded.roleList[1]._links.roles.href").value(BASE_PATH))
            .andExpect(jsonPath("$._embedded.roleList[2].id").value(user.getId()))
            .andExpect(jsonPath("$._embedded.roleList[2].name").value(user.getName()))
            .andExpect(jsonPath("$._embedded.roleList[2]._links.self.href").value(BASE_PATH + "/" + user.getId()))
            .andExpect(jsonPath("$._embedded.roleList[2]._links.roles.href").value(BASE_PATH))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH));
    }

    @Test
    public void testGetAllRolesWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/roles"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetById() throws Exception
    {
        Role admin = new Role(1, Role.Unit.ROLE_ADMIN);

        when(roleService.getById(admin.getId())).thenReturn(Optional.of(admin));

        mockMvc.perform(get("/roles/" + admin.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(admin.getId()))
            .andExpect(jsonPath("$.name").value(admin.getName()))
            .andExpect(jsonPath("$._links.self.href").value(BASE_PATH + "/" + admin.getId()))
            .andExpect(jsonPath("$._links.roles.href").value(BASE_PATH));
    }

    @Test
    public void testGetByIdWhenNotAuthenticated() throws Exception
    {
        mockMvc.perform(get("/roles/1"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access Denied"));
    }

    @Test
    @WithMockUser
    public void testGetUnknownRoleById() throws Exception
    {
        mockMvc.perform(get("/roles/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("The role was not found."));
    }
}
