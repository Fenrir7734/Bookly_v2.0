package com.fenrir.masterdetail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fenrir.masterdetail.dto.ReviewRequestDTO;
import com.fenrir.masterdetail.repository.ReviewRepository;
import com.fenrir.masterdetail.setup.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = {
        "classpath:db/ReviewControllerTest.sql"
})
class ReviewControllerTest extends IntegrationTest {
    private static final String REVIEW_CONTROLLER_ENDPOINT = "/api/reviews";
    private static final String GET_REVIEW_BY_USERNAME_AND_BOOK_ID = REVIEW_CONTROLLER_ENDPOINT + "/{username}/{bookId}";
    private static final String GET_REVIEW_BY_BOOK_ID = REVIEW_CONTROLLER_ENDPOINT + "/book/{bookId}";
    private static final String GET_REVIEW_BY_USERNAME = REVIEW_CONTROLLER_ENDPOINT + "/user/{username}";
    private static final String GET_BOOK_STATISTICS = REVIEW_CONTROLLER_ENDPOINT + "/book/{bookId}/stats";
    private static final String POST_REVIEW = REVIEW_CONTROLLER_ENDPOINT + "/{username}/{bookId}";
    private static final String UPDATE_REVIEW = REVIEW_CONTROLLER_ENDPOINT + "/{username}/{bookId}";
    private static final String DELETE_REVIEW = REVIEW_CONTROLLER_ENDPOINT + "/{username}/{bookId}";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    private static final int INITIAL_REVIEW_COUNT = 1;

    private static final int BOOK_1_ID = 101;
    private static final int BOOK_2_ID = 102;
    private static final int USER_1_ID = 101;
    private static final int USER_2_ID = 102;
    private static final String USER_1_USERNAME = "nowak";
    private static final String USER_2_USERNAME = "kowalski";

    @Test
    public void getReviewByUsernameAndBookId_should_return_review() throws Exception {
        mockMvc.perform(get(GET_REVIEW_BY_USERNAME_AND_BOOK_ID, USER_1_USERNAME, BOOK_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id.userId", is(USER_1_ID)))
                .andExpect(jsonPath("$.id.bookId", is(BOOK_1_ID)))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.rate").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.book").isNotEmpty())
                .andExpect(jsonPath("$.user").isNotEmpty());
    }

    @Test
    public void getReviewByUsernameAndBookId_should_fail_when_given_wrong_username() throws Exception {
        mockMvc.perform(get(GET_REVIEW_BY_USERNAME_AND_BOOK_ID, "User", BOOK_1_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void getReviewByUsernameAndBookId_should_fail_when_given_wrong_bookId() throws Exception {
        mockMvc.perform(get(GET_REVIEW_BY_USERNAME_AND_BOOK_ID, USER_1_USERNAME, 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void getReviewByBookId_should_return_all_book_reviews() throws Exception {
        mockMvc.perform(get(GET_REVIEW_BY_BOOK_ID, BOOK_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id.userId", is(USER_1_ID)))
                .andExpect(jsonPath("$.content[0].id.bookId", is(BOOK_1_ID)))
                .andExpect(jsonPath("$.content[0].content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].rate").isNotEmpty())
                .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].book").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user").isNotEmpty());
    }

    @Test
    public void getReviewByUsername_should_return_all_user_reviews() throws Exception {
        mockMvc.perform(get(GET_REVIEW_BY_USERNAME, USER_1_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id.userId", is(USER_1_ID)))
                .andExpect(jsonPath("$.content[0].id.bookId", is(BOOK_1_ID)))
                .andExpect(jsonPath("$.content[0].content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].rate").isNotEmpty())
                .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].book").isNotEmpty())
                .andExpect(jsonPath("$.content[0].user").isNotEmpty());
    }

    @Test
    public void getBookStatistics_should_return_book_statistics() throws Exception {
        mockMvc.perform(get(GET_BOOK_STATISTICS, BOOK_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(BOOK_1_ID)))
                .andExpect(jsonPath("$.numberOfRates").isNotEmpty())
                .andExpect(jsonPath("$.numberOfComments").isNotEmpty())
                .andExpect(jsonPath("$.rate").isNotEmpty());
    }

    @Test
    @WithUserDetails(value = USER_1_USERNAME)
    public void postShelf_should_create_new_review_for_user() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 5);

        mockMvc.perform(post(POST_REVIEW, USER_1_USERNAME, BOOK_2_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id.userId", is(USER_1_ID)))
                .andExpect(jsonPath("$.id.bookId", is(BOOK_2_ID)))
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.rate", is(5)));

        int size = reviewRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_REVIEW_COUNT + 1);
    }

    @Test
    @WithUserDetails(value = USER_2_USERNAME)
    public void postShelf_should_fail_when_given_not_currently_logged_user() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 5);

        mockMvc.perform(post(POST_REVIEW, USER_1_USERNAME, BOOK_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());

        int size = reviewRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_REVIEW_COUNT);
    }

    @Test
    @WithUserDetails(value = USER_1_USERNAME)
    public void postShelf_should_fail_when_given_wrong_bookId() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 5);

        mockMvc.perform(post(POST_REVIEW, USER_1_USERNAME, 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());

        int size = reviewRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_REVIEW_COUNT);
    }

    @Test
    @WithUserDetails(value = USER_1_USERNAME)
    public void updateShelf_should_update_existing_review() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 1);

        mockMvc.perform(put(UPDATE_REVIEW, USER_1_USERNAME, BOOK_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.rate", is(1)));
    }

    @Test
    @WithUserDetails(value = USER_2_USERNAME)
    public void updateShelf_should_fail_when_given_not_currently_logged_user() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 5);

        mockMvc.perform(put(UPDATE_REVIEW, USER_1_USERNAME, BOOK_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = USER_1_USERNAME)
    public void updateShelf_should_fail_when_given_wrong_bookId() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 5);

        mockMvc.perform(put(UPDATE_REVIEW, USER_1_USERNAME, 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithUserDetails(value = USER_1_USERNAME)
    public void deleteShelf_should_delete_review() throws Exception {
        mockMvc.perform(delete(DELETE_REVIEW, USER_1_USERNAME, BOOK_1_ID))
                .andExpect(status().isNoContent());

        int size = reviewRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_REVIEW_COUNT - 1);
    }

    @Test
    @WithUserDetails(value = USER_2_USERNAME)
    public void deleteShelf_should_fail_when_given_not_currently_logged_user() throws Exception {
        mockMvc.perform(delete(DELETE_REVIEW, USER_1_USERNAME, BOOK_1_ID))
                .andExpect(status().isForbidden());

        int size = reviewRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_REVIEW_COUNT);
    }

    @Test
    @WithUserDetails(value = USER_1_USERNAME)
    public void deleteShelf_should_fail_when_given_wrong_bookId() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO("Content", 5);

        mockMvc.perform(delete(DELETE_REVIEW, USER_1_USERNAME, 123L))
                .andExpect(status().isNotFound());

        int size = reviewRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_REVIEW_COUNT);
    }
}