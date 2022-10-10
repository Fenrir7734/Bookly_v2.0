package com.fenrir.masterdetail.service;

import com.fenrir.masterdetail.exception.ResourceNotFoundException;
import com.fenrir.masterdetail.model.Book;
import com.fenrir.masterdetail.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    public void getById_should_return_Book_when_given_existing_id() {
        final long id = 1L;
        Book expectedBook = Book.builder()
                .id(id)
                .title("Title 1")
                .build();

        given(bookRepository.findById(id))
                .willReturn(Optional.of(expectedBook));

        Book actualBook = bookService.get(id);

        assertThat(actualBook)
                .isEqualTo(expectedBook);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void getById_should_throw_exception_when_given_wrong_id() {
        final long id = 1L;

        given(bookRepository.findById(id))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.get(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Book was not found for id=%d", id));
        Mockito.verify(bookRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void getAll_should_return_all_books() {
        Book book1 = Book
                .builder()
                .id(1L)
                .title("Title 1")
                .build();

        Book book2 = Book
                .builder()
                .id(2L)
                .title("Title 2")
                .build();

        Page<Book> expectedPage = new PageImpl<>(List.of(book1, book2));
        Pageable pageable = PageRequest.of(1, 10);

        given(bookRepository.findAll(pageable))
                .willReturn(expectedPage);

        Page<Book> actualPage = bookService.getAll(pageable);

        assertThat(actualPage)
                .isEqualTo(expectedPage);
        Mockito.verify(bookRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void getAll_should_return_empty_page_when_there_is_no_books() {
        Pageable pageable = PageRequest.of(1, 10);

        given(bookRepository.findAll(pageable))
                .willReturn(Page.empty(pageable));

        Page<Book> actualPage = bookService.getAll(pageable);

        assertThat(actualPage.get())
                .isEmpty();
        Mockito.verify(bookRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void create_should_create_new_book() {
        Book book = Book.builder()
                .title("Title")
                .author("Author")
                .build();

        Book expectedBook = Book
                .builder()
                .id(1L)
                .title("Title")
                .author("Author")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(bookRepository.save(book))
                .willReturn(expectedBook);

        Book actualBook = bookService.create(book);

        assertThat(actualBook)
                .isEqualTo(expectedBook);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    public void create_should_ignore_id_when_saving_book() {
        Book book = Book.builder()
                .title("Title")
                .author("Author")
                .build();

        Book expectedBook = Book
                .builder()
                .id(1L)
                .title("Title")
                .author("Author")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(bookRepository.save(book))
                .willReturn(expectedBook);

        book.setId(5L);

        Book actualBook = bookService.create(book);

        assertThat(actualBook)
                .isEqualTo(expectedBook);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    public void update_should_update_existing_book() {
        Book bookToUpdate = Book.builder()
                .id(1L)
                .title("Title")
                .author("Author")
                .description("Desc")
                .cover("cover")
                .updatedAt(LocalDateTime.of(2022, 11, 10, 0, 0, 0))
                .createdAt(LocalDateTime.of(2022, 11, 10, 0, 0, 0))
                .build();

        Book updatedBook = Book
                .builder()
                .title("Title 2")
                .author("Author 2")
                .description("Desc 3")
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.of(2022, 11, 10, 0, 0, 0))
                .build();

        Book bookAfterUpdate = Book
                .builder()
                .id(1L)
                .title("Title 2")
                .author("Author 2")
                .description("Desc 3")
                .cover(null)
                .updatedAt(LocalDateTime.of(2022, 11, 10, 0, 0, 0))
                .createdAt(LocalDateTime.of(2022, 11, 10, 0, 0, 0))
                .build();

        given(bookRepository.findById(1L))
                .willReturn(Optional.of(bookToUpdate));
        given(bookRepository.save(bookAfterUpdate))
                .willReturn(bookAfterUpdate);

        Book actualBook = bookService.update(1L, updatedBook);

        assertThat(actualBook)
                .isEqualTo(bookAfterUpdate);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookRepository, Mockito.times(1)).save(bookAfterUpdate);
    }

    @Test
    public void update_should_throw_exception_when_given_wrong_id() {
        given(bookRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(1L, new Book()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book was not found for id=1");
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void delete_should_delete_book() {
        Book book = Book.builder().id(1L).build();

        given(bookRepository.findById(1L))
                .willReturn(Optional.of(book));
        willDoNothing().given(bookRepository).delete(book);

        bookService.delete(1L);

        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    public void delete_should_throw_exception_given_wrong_id() {
        given(bookRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book was not found for id=1");
        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }
}