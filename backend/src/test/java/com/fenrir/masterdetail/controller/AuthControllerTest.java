package com.fenrir.masterdetail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fenrir.masterdetail.dto.JwtTokenDTO;
import com.fenrir.masterdetail.dto.SignInDTO;
import com.fenrir.masterdetail.dto.SignUpDTO;
import com.fenrir.masterdetail.repository.UserRepository;
import com.fenrir.masterdetail.setup.IntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = {
        "classpath:db/UserControllerTest.sql"
})
class AuthControllerTest extends IntegrationTest {
    private static final String AUTH_CONTROLLER_ENDPOINT = "/api/auth";
    private static final String REGISTER_ENDPOINT = AUTH_CONTROLLER_ENDPOINT + "/register";
    private static final String LOGIN_ENDPOINT = AUTH_CONTROLLER_ENDPOINT + "/login";
    private static final String VALIDATE_TOKEN_ENDPOINT = AUTH_CONTROLLER_ENDPOINT + "/valid";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final int INITIAL_USER_COUNT = 2;

    private static final int USER_1_ID = 101;
    private static final String USER_1_USERNAME = "kowalski";
    private static final String USER_1_EMAIL = "jan.kowalski@gmail.com";
    private static final String USER_1_PASSWORD = "password123";

    @Test
    public void signup_should_return_user() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO(
                "John",
                "Smith",
                "smith",
                "smith@gmail.com",
                "password"
        );

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname", is(signUpDTO.getFirstname())))
                .andExpect(jsonPath("$.lastname", is(signUpDTO.getLastname())))
                .andExpect(jsonPath("$.username", is(signUpDTO.getUsername())))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        int size = userRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_USER_COUNT + 1);
    }

    @Test
    public void signup_should_fail_when_given_duplicate_credentials() throws Exception {
        SignUpDTO signUpDTO = new SignUpDTO(
                "John",
                "Smith",
                USER_1_USERNAME,
                "smith@gmail.com",
                "password"
        );

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode", is(409)))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        int size = userRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_USER_COUNT);
    }

    @Test
    public void signIn_should_return_token() throws Exception {
        SignInDTO signInDTO = new SignInDTO(USER_1_USERNAME, USER_1_PASSWORD);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType", is("Bearer")));
    }

    @Test
    public void signIn_should_fail_when_given_invalid_credentials() throws Exception {
        SignInDTO signInDTO = new SignInDTO(USER_1_USERNAME, "Pass");

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode", is(401)))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    public void validateToken_should_pass_when_given_valid_token() throws Exception {
        SignInDTO signInDTO = new SignInDTO(USER_1_USERNAME, USER_1_PASSWORD);

        MvcResult result = mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        JwtTokenDTO tokenDTO = new JwtTokenDTO(token);

        mockMvc.perform(post(VALIDATE_TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void validateToken_should_fail_when_given_wrong_token() throws Exception {
        JwtTokenDTO tokenDTO = new JwtTokenDTO("Invalid token");

        mockMvc.perform(post(VALIDATE_TOKEN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isUnauthorized());
    }
}