package com.fenrir.masterdetail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fenrir.masterdetail.model.Book;
import com.fenrir.masterdetail.repository.BookRepository;
import com.fenrir.masterdetail.setup.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Sql(value = {
        "classpath:db/BookControllerTest.sql"
})
class BookControllerTest extends IntegrationTest {
    private static final String BOOK_CONTROLLER_ENDPOINT = "/api/books";
    private static final String GET_BY_ID_ENDPOINT = BOOK_CONTROLLER_ENDPOINT + "/{id}";
    private static final String UPDATE_BOOK_ENDPOINT = BOOK_CONTROLLER_ENDPOINT + "/{id}";
    private static final String DELETE_BOOK_ENDPOINT = BOOK_CONTROLLER_ENDPOINT + "/{id}";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    private static final int INITIAL_BOOK_COUNT = 2;

    private static final long BOOK_1_ID = 101;
    private static final String BOOK_1_AUTHOR = "George R.R. Martin";
    private static final String BOOK_1_TITLE = "A Game of Thrones";

    private static final long BOOK_2_ID = 102;
    private static final String BOOK_2_AUTHOR = "James S.A. Corey";
    private static final String BOOK_2_TITLE = "Calibans War";

    @Test
    public void getBookById_should_return_book() throws Exception {
        mockMvc.perform(get(GET_BY_ID_ENDPOINT, BOOK_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) BOOK_1_ID)))
                .andExpect(jsonPath("$.title", is(BOOK_1_TITLE)))
                .andExpect(jsonPath("$.author", is(BOOK_1_AUTHOR)))
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.cover").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    public void getAllBooks_should_return_book_page() throws Exception {
        mockMvc.perform(get(BOOK_CONTROLLER_ENDPOINT + "?sort=id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id", is((int) BOOK_1_ID)))
                .andExpect(jsonPath("$.content[0].title", is(BOOK_1_TITLE)))
                .andExpect(jsonPath("$.content[0].author", is(BOOK_1_AUTHOR)))
                .andExpect(jsonPath("$.content[0].description").isNotEmpty())
                .andExpect(jsonPath("$.content[0].cover").isNotEmpty())
                .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[1].id", is((int) BOOK_2_ID)))
                .andExpect(jsonPath("$.content[1].title", is(BOOK_2_TITLE)))
                .andExpect(jsonPath("$.content[1].author", is(BOOK_2_AUTHOR)))
                .andExpect(jsonPath("$.content[1].description").isNotEmpty())
                .andExpect(jsonPath("$.content[1].cover").isNotEmpty())
                .andExpect(jsonPath("$.content[1].updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.content[1].createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(value = "user", roles = "USER")
    public void postBook_should_create_new_book() throws Exception {
        Book book = Book.builder()
                .title("New book")
                .author("Author")
                .cover("cover link")
                .build();

        mockMvc.perform(post(BOOK_CONTROLLER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())))
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.cover", is(book.getCover())))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        int size = bookRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_BOOK_COUNT + 1);
    }

    @Test
    public void postBook_should_fail_for_anonymous_user() throws Exception {
        Book book = Book.builder()
                .title("New book")
                .author("Author")
                .cover("cover link")
                .build();

        mockMvc.perform(post(BOOK_CONTROLLER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isUnauthorized());

        int size = bookRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_BOOK_COUNT);
    }

    @Test
    @WithMockUser(value = "admin", roles = "ADMIN")
    public void updateBook_should_update_existing_book() throws Exception {
        Book book = Book.builder()
                .title("New book")
                .author("Author")
                .cover("cover link")
                .build();

        mockMvc.perform(put(UPDATE_BOOK_ENDPOINT, BOOK_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) BOOK_1_ID)))
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())))
                .andExpect(jsonPath("$.description").isEmpty())
                .andExpect(jsonPath("$.cover", is(book.getCover())))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(value = "user", roles = "USER")
    public void updateBook_should_fail_for_ordinary_user() throws Exception {
        Book book = Book.builder()
                .title("New book")
                .author("Author")
                .cover("cover link")
                .build();

        mockMvc.perform(put(UPDATE_BOOK_ENDPOINT, BOOK_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "admin", roles = "ADMIN")
    public void updateBook_should_fail_when_given_wrong_id() throws Exception {
        Book book = Book.builder()
                .title("New book")
                .author("Author")
                .cover("cover link")
                .build();

        mockMvc.perform(put(UPDATE_BOOK_ENDPOINT, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(value = "admin", roles = "ADMIN")
    public void deleteBookById_should_delete_book() throws Exception {
        mockMvc.perform(delete(DELETE_BOOK_ENDPOINT, BOOK_1_ID))
                .andExpect(status().isNoContent());

        int size = bookRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_BOOK_COUNT - 1);
    }

    @Test
    @WithMockUser(value = "user", roles = "USER")
    public void deleteBookById_should_fail_for_ordinary_user() throws Exception {
        mockMvc.perform(delete(DELETE_BOOK_ENDPOINT, BOOK_1_ID))
                .andExpect(status().isForbidden());

        int size = bookRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_BOOK_COUNT);
    }

    @Test
    @WithMockUser(value = "admin", roles = "ADMIn")
    public void deleteBookById_should_fail_when_given_wrong_id() throws Exception {
        mockMvc.perform(delete(DELETE_BOOK_ENDPOINT, 1L))
                .andExpect(status().isForbidden());

        int size = bookRepository.findAll().size();
        assertThat(size)
                .isEqualTo(INITIAL_BOOK_COUNT);
    }
}