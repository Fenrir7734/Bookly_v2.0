package com.fenrir.masterdetail.controller;

import com.fenrir.masterdetail.model.Role;
import com.fenrir.masterdetail.model.User;
import com.fenrir.masterdetail.repository.UserRepository;
import com.fenrir.masterdetail.setup.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = {
        "classpath:db/UserControllerTest.sql"
})
class UserControllerTest extends IntegrationTest {
    private static final String USER_CONTROLLER_ENDPOINT = "/api/users";
    private static final String GET_BY_USERNAME_ENDPOINT = USER_CONTROLLER_ENDPOINT + "/{username}";
    private static final String GRANT_ROLE_ENDPOINT = USER_CONTROLLER_ENDPOINT + "/{username}/grant/{role}";
    private static final String DELETE_ENDPOINT = USER_CONTROLLER_ENDPOINT + "/{username}";

    @Autowired
    private UserRepository userRepository;

    private static final int INITIAL_USER_COUNT = 2;

    private static final int USER_1_ID = 101;
    private static final String USER_1_USERNAME = "kowalski";
    private static final String USER_1_EMAIL = "jan.kowalski@gmail.com";
    private static final String USER_1_PASSWORD = "password123";
    private static final String USER_1_FIRSTNAME = "Jan";
    private static final String USER_1_LASTNAME = "Kowalski";
    private static final String USER_1_ROLE = "ROLE_ADMIN";

    private static final int USER_2_ID = 102;
    private static final String USER_2_USERNAME = "nowak";
    private static final String USER_2_EMAIL = "adam.nowak@gmail.com";
    private static final String USER_2_PASSWORD = "password123";
    private static final String USER_2_FIRSTNAME = "Adam";
    private static final String USER_2_LASTNAME = "Nowak";
    private static final String USER_2_ROLE = "ROLE_USER";


    @Test
    public void getUserByUsername_should_return_user() throws Exception {
        mockMvc.perform(get(GET_BY_USERNAME_ENDPOINT, USER_1_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is(USER_1_FIRSTNAME)))
                .andExpect(jsonPath("$.lastname", is(USER_1_LASTNAME)))
                .andExpect(jsonPath("$.username", is(USER_1_USERNAME)))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    public void getUserByUsername_should_fail_when_given_wrong_username() throws Exception {
        mockMvc.perform(get(GET_BY_USERNAME_ENDPOINT, "User"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers_should_return_users_page() throws Exception {
        mockMvc.perform(get(USER_CONTROLLER_ENDPOINT + "?sort=username,asc"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.page.totalElements", is(INITIAL_USER_COUNT)))
                .andExpect(jsonPath("$._embedded.Users").isNotEmpty())
                .andExpect(jsonPath("$._embedded.Users[0].firstname", is(USER_1_FIRSTNAME)))
                .andExpect(jsonPath("$._embedded.Users[0].lastname", is(USER_1_LASTNAME)))
                .andExpect(jsonPath("$._embedded.Users[0].username", is(USER_1_USERNAME)))
                .andExpect(jsonPath("$._embedded.Users[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$._embedded.Users[1].firstname", is(USER_2_FIRSTNAME)))
                .andExpect(jsonPath("$._embedded.Users[1].lastname", is(USER_2_LASTNAME)))
                .andExpect(jsonPath("$._embedded.Users[1].username", is(USER_2_USERNAME)))
                .andExpect(jsonPath("$._embedded.Users[1].createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(value = "admin", roles = "ADMIN")
    public void grantRole_should_change_user_role() throws Exception {
        mockMvc.perform(put(GRANT_ROLE_ENDPOINT, USER_1_USERNAME, "ROLE_USER"))
                .andExpect(status().isNoContent());

        User user = userRepository.getById((long) USER_1_ID);

        assertThat(user.getRole())
                .isEqualTo(Role.ROLE_USER);
    }

    @Test
    @WithMockUser(value = "user", roles = "USER")
    public void grantRole_should_should_fail_for_user() throws Exception {
        mockMvc.perform(put(GRANT_ROLE_ENDPOINT, USER_1_USERNAME, "ROLE_USER"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "admin", roles = "ADMIN")
    public void deleteUserByUsername_should_delete_user() throws Exception {
        mockMvc.perform(delete(DELETE_ENDPOINT, USER_2_USERNAME))
                .andExpect(status().isNoContent());

        int size = userRepository.findAll().size();

        assertThat(size)
                .isEqualTo(INITIAL_USER_COUNT - 1);
    }

    @Test
    @WithMockUser(value = "user", roles = "USER")
    public void deleteUserByUsername_should_fail_for_user() throws Exception {
        mockMvc.perform(delete(DELETE_ENDPOINT, USER_2_USERNAME))
                .andExpect(status().isForbidden());
    }
}