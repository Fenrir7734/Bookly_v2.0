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
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private ReviewMapper reviewMapper;

    public Review get(String username, Long bookId) {
        return getByUsernameAndBookId(username, bookId);
    }

    public Page<Review> getAll(String username, Pageable pageable) {
        return reviewRepository.findAllByUser_Username(username, pageable);
    }

    public Page<Review> getAll(Long bookId, Pageable pageable) {
        return reviewRepository.findAllByBook_Id(bookId, pageable);
    }

    public StatisticsDTO getBooksStatistics(Long bookId) {
        List<Review> shelves = reviewRepository.findAllByBook_Id(bookId);
        return reviewMapper.toStatisticsDTO(bookId, shelves);
    }

    public Review create(ReviewRequestDTO request, String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User was not found for username=%s", username)
                ));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Book was not found for id=%s", bookId)
                ));
        Review review = reviewMapper.fromReviewRequestDTO(request, user, book);
        return reviewRepository.save(review);
    }

    public Review update(ReviewRequestDTO request, String username, Long bookId) {
        Review reviewToUpdate = getByUsernameAndBookId(username, bookId);
        reviewToUpdate.setRate(request.getRate());
        reviewToUpdate.setContent(request.getContent());
        return reviewRepository.save(reviewToUpdate);
    }

    public void delete(String username, Long bookId) {
        Review review = getByUsernameAndBookId(username, bookId);
        reviewRepository.delete(review);
    }

    public Review getByUsernameAndBookId(String username, Long bookId) {
        return reviewRepository.findByUser_UsernameAndBookId(username, bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Review was not found for user=%s and bookId=%s", username, bookId)
                ));
    }
}
