package com.fenrir.masterdetail.service;

import com.fenrir.masterdetail.dto.ReviewRequestDTO;
import com.fenrir.masterdetail.dto.StatisticsDTO;
import com.fenrir.masterdetail.dto.mapper.ReviewMapper;
import com.fenrir.masterdetail.exception.ResourceNotFoundException;
import com.fenrir.masterdetail.model.Book;
import com.fenrir.masterdetail.model.Review;
import com.fenrir.masterdetail.model.User;
import com.fenrir.masterdetail.repository.BookRepository;
import com.fenrir.masterdetail.repository.ReviewRepository;
import com.fenrir.masterdetail.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "user123";
    private static final Long BOOK_ID = 1L;

    private User user;
    private Book book;
    private Review review;

    @BeforeEach
    public void setUp() {
        this.user = User.builder()
                .id(USER_ID)
                .firstname("John")
                .lastname("Smith")
                .username(USERNAME)
                .email("smith@gmail.com")
                .build();

        this.book = Book.builder()
                .id(BOOK_ID)
                .title("Book_1")
                .build();

        this.review = Review.builder()
                .id(new Review.Id(USER_ID, BOOK_ID))
                .rate(5)
                .updatedAt(LocalDateTime.of(2022, 1, 1, 1, 0, 0))
                .createdAt(LocalDateTime.of(2022, 1, 1, 1, 0, 0))
                .book(book)
                .user(user)
                .build();
    }

    @Test
    public void get_should_return_review_given_correct_username_and_bookId() {
        given(reviewRepository.findByUser_UsernameAndBookId(USERNAME, BOOK_ID))
                .willReturn(Optional.of(review));

        Review actualReview = reviewService.get(USERNAME, USER_ID);

        assertThat(actualReview)
                .isEqualTo(review);
        verify(reviewRepository, times(1)).findByUser_UsernameAndBookId(USERNAME, BOOK_ID);
    }

    @Test
    public void get_should_throw_exception_given_wrong_username() {
        final String wrongUsername = "Username";

        given(reviewRepository.findByUser_UsernameAndBookId(wrongUsername, BOOK_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.get(wrongUsername, BOOK_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Review was not found for user=%s and bookId=%s", wrongUsername, BOOK_ID));
    }

    @Test
    public void get_should_throw_exception_given_wrong_book_id() {
        final long wrongBookId = 2L;

        given(reviewRepository.findByUser_UsernameAndBookId(USERNAME, wrongBookId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.get(USERNAME, wrongBookId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Review was not found for user=%s and bookId=%s", USERNAME, wrongBookId));
    }

    @Test
    public void getAll_should_return_all_user_reviews_when_given_correct_username() {
        Page<Review> expectedPage = new PageImpl<>(List.of(review));
        Pageable pageable = PageRequest.of(1, 10);

        given(reviewRepository.findAllByUser_Username(USERNAME, pageable))
                .willReturn(expectedPage);

        Page<Review> actualPage = reviewService.getAll(USERNAME, pageable);

        assertThat(actualPage)
                .isEqualTo(expectedPage);
        verify(reviewRepository, times(1)).findAllByUser_Username(USERNAME, pageable);
    }

    @Test
    public void getAll_should_return_empty_page_when_given_wrong_username() {
        final String wrongUsername = "Username";

        Page<Review> expectedPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(1, 10);

        given(reviewRepository.findAllByUser_Username(wrongUsername, pageable))
                .willReturn(expectedPage);

        Page<Review> actualPage = reviewService.getAll(wrongUsername, pageable);

        assertThat(actualPage)
                .isEqualTo(expectedPage);
        assertThat(actualPage.get())
                .isEmpty();
        verify(reviewRepository, times(1)).findAllByUser_Username(wrongUsername, pageable);
    }

    @Test
    public void getAll_should_return_all_book_reviews_when_given_correct_bookId() {
        Page<Review> expectedPage = new PageImpl<>(List.of(review));
        Pageable pageable = PageRequest.of(1, 10);

        given(reviewRepository.findAllByBook_Id(BOOK_ID, pageable))
                .willReturn(expectedPage);

        Page<Review> actualPage = reviewService.getAll(BOOK_ID, pageable);

        assertThat(actualPage)
                .isEqualTo(expectedPage);
        verify(reviewRepository, times(1)).findAllByBook_Id(BOOK_ID, pageable);
    }

    @Test
    public void getAll_should_return_empty_page_when_given_wrong_bookId() {
        final long bookId = 1L;

        Page<Review> expectedPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(1, 10);

        given(reviewRepository.findAllByBook_Id(bookId, pageable))
                .willReturn(expectedPage);

        Page<Review> actualPage = reviewService.getAll(bookId, pageable);

        assertThat(actualPage)
                .isEqualTo(expectedPage);
        assertThat(actualPage.get())
                .isEmpty();
        verify(reviewRepository, times(1)).findAllByBook_Id(bookId, pageable);
    }

    @Test
    public void getBooksStatistics_should_return_book_statistics_given_correct_bookId() {
        List<Review> reviews = List.of(review);
        StatisticsDTO expectedStatisticsDto = new StatisticsDTO(BOOK_ID, 1L, 0L, 5d);

        given(reviewRepository.findAllByBook_Id(BOOK_ID))
                .willReturn(reviews);
        given(reviewMapper.toStatisticsDTO(BOOK_ID, reviews))
                .willReturn(expectedStatisticsDto);

        StatisticsDTO actualStatisticsDto = reviewService.getBooksStatistics(BOOK_ID);

        assertThat(actualStatisticsDto)
                .isEqualTo(expectedStatisticsDto);
        verify(reviewRepository, times(1)).findAllByBook_Id(BOOK_ID);
        verify(reviewMapper, times(1)).toStatisticsDTO(BOOK_ID, reviews);
    }

    @Test
    public void create_should_create_new_review() {
        final String content = "Content";
        final int rate = 5;

        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(content, rate);
        Review newReview = Review.builder()
                .id(new Review.Id(USER_ID, BOOK_ID))
                .content(content)
                .rate(rate)
                .build();
        Review savedReview = Review.builder()
                .id(new Review.Id(USER_ID, BOOK_ID))
                .content(content)
                .rate(rate)
                .updatedAt(LocalDateTime.of(2022, 1, 1, 1, 0, 0))
                .createdAt(LocalDateTime.of(2022, 1, 1, 1, 0, 0))
                .book(book)
                .user(user)
                .build();

        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));
        given(bookRepository.findById(BOOK_ID))
                .willReturn(Optional.of(book));
        given(reviewMapper.fromReviewRequestDTO(reviewRequestDTO, user, book))
                .willReturn(newReview);
        given(reviewRepository.save(newReview))
                .willReturn(savedReview);

        Review actualReview = reviewService.create(reviewRequestDTO, USERNAME, BOOK_ID);

        assertThat(actualReview)
                .isEqualTo(savedReview);
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(reviewMapper, times(1)).fromReviewRequestDTO(reviewRequestDTO, user, book);
        verify(reviewRepository, times(1)).save(newReview);
    }

    @Test
    public void create_should_throw_exception_when_given_wrong_username() {
        final String wrongUsername = "User";

        given(userRepository.findByUsername(wrongUsername))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(new ReviewRequestDTO(null, 5), wrongUsername, BOOK_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User was not found for username=%s", wrongUsername));
        verify(userRepository, times(1)).findByUsername(wrongUsername);
    }

    @Test
    public void create_should_throw_exception_when_given_wrong_bookId() {
        final long wrongBookId = 2L;

        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(user));
        given(bookRepository.findById(wrongBookId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(new ReviewRequestDTO(null, 5), USERNAME, wrongBookId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Book was not found for id=%s", wrongBookId));
        verify(bookRepository, times(1)).findById(wrongBookId);
    }

    @Test
    public void update_should_update_review_when_given_correct_username_and_bookId() {
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(null, 1);

        Review reviewToUpdate = Review.builder()
                .id(new Review.Id(USER_ID, BOOK_ID))
                .user(user)
                .book(book)
                .rate(5)
                .content("content")
                .build();

        Review reviewAfterUpdate = Review.builder()
                .id(new Review.Id(USER_ID, BOOK_ID))
                .user(user)
                .book(book)
                .rate(reviewRequestDTO.getRate())
                .content(reviewRequestDTO.getContent())
                .build();

        given(reviewRepository.findByUser_UsernameAndBookId(USERNAME, BOOK_ID))
                .willReturn(Optional.of(reviewToUpdate));
        given(reviewRepository.save(reviewAfterUpdate))
                .willReturn(reviewAfterUpdate);

        Review actualReview = reviewService.update(reviewRequestDTO, USERNAME, BOOK_ID);

        assertThat(actualReview)
                .isEqualTo(reviewAfterUpdate);
        verify(reviewRepository, times(1)).findByUser_UsernameAndBookId(USERNAME, BOOK_ID);
        verify(reviewRepository, times(1)).save(reviewAfterUpdate);
    }

    @Test
    public void update_should_throw_exception_when_given_wrong_username() {
        final String wrongUsername = "User";

        given(reviewRepository.findByUser_UsernameAndBookId(wrongUsername, BOOK_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.update(new ReviewRequestDTO(null, 5), wrongUsername, BOOK_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Review was not found for user=%s and bookId=%s", wrongUsername, BOOK_ID));
    }

    @Test
    public void update_should_throw_exception_when_given_wrong_bookId() {
        final long wrongBookId = 2L;

        given(reviewRepository.findByUser_UsernameAndBookId(USERNAME, wrongBookId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.update(new ReviewRequestDTO(null, 5), USERNAME, wrongBookId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Review was not found for user=%s and bookId=%s", USERNAME, wrongBookId));
    }

    @Test
    public void delete_should_delete_review_when_given_correct_username_bookId() {
        given(reviewRepository.findByUser_UsernameAndBookId(USERNAME, BOOK_ID))
                .willReturn(Optional.of(review));
        willDoNothing().given(reviewRepository).delete(review);

        reviewService.delete(USERNAME, BOOK_ID);

        verify(reviewRepository, times(1)).findByUser_UsernameAndBookId(USERNAME, BOOK_ID);
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    public void delete_should_throw_exception_when_given_wrong_username() {
        final String wrongUsername = "User";

        given(reviewRepository.findByUser_UsernameAndBookId(wrongUsername, BOOK_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(wrongUsername, BOOK_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Review was not found for user=%s and bookId=%s", wrongUsername, BOOK_ID));
    }

    @Test
    public void delete_should_throw_exception_when_given_wrong_bookId() {
        final long wrongBookId = 2L;

        given(reviewRepository.findByUser_UsernameAndBookId(USERNAME, wrongBookId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(USERNAME, wrongBookId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Review was not found for user=%s and bookId=%s", USERNAME, wrongBookId));
    }
}