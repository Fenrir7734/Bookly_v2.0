package com.fenrir.masterdetail.repository;

import com.fenrir.masterdetail.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUser_UsernameAndBookId(String username, Long bookId);
    Page<Review> findAllByUser_Username(String username, Pageable pageable);
    Page<Review> findAllByBook_Id(Long bookId, Pageable pageable);
    List<Review> findAllByBook_Id(Long bookId);
}
