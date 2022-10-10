package com.fenrir.masterdetail.controller;

import com.fenrir.masterdetail.dto.ReviewRequestDTO;
import com.fenrir.masterdetail.dto.StatisticsDTO;
import com.fenrir.masterdetail.model.Review;
import com.fenrir.masterdetail.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(
        path = "/api/reviews",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ReviewController {
    private ReviewService reviewService;

    @GetMapping(path = "/{username}/{bookId}")
    public ResponseEntity<?> getReviewByUsernameAndBookId(
            @PathVariable("username") String username,
            @PathVariable("bookId") Long bookId) {

        Review review = reviewService.get(username, bookId);
        return ResponseEntity.ok(review);
    }

    @GetMapping(path = "/book/{bookId}")
    public ResponseEntity<?> getReviewByBookId(
            @PathVariable("bookId") Long bookId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Review> reviews = reviewService.getAll(bookId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping(path = "/user/{username}")
    public ResponseEntity<?> getReviewByUsername(
            @PathVariable("username") String username,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Review> reviews = reviewService.getAll(username, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/book/{bookId}/stats")
    public ResponseEntity<?> getBookStatistics(@PathVariable("bookId") Long bookId) {
        StatisticsDTO stats = reviewService.getBooksStatistics(bookId);
        return ResponseEntity.ok(stats);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(path = "/{username}/{bookId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postShelf(
            @PathVariable("username") String username,
            @PathVariable("bookId") Long bookId,
            @RequestBody ReviewRequestDTO requestDTO) {

        Review review = reviewService.create(requestDTO, username, bookId);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(path = "/{username}/{bookId}")
    public ResponseEntity<?> updateShelf(
            @PathVariable("username") String username,
            @PathVariable("bookId") Long bookId,
            @RequestBody ReviewRequestDTO requestDTO) {

        Review review = reviewService.update(requestDTO, username, bookId);
        return ResponseEntity.ok(review);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(path = "/{username}/{bookId}")
    public ResponseEntity<?> deleteShelfByUsernameAndBookId(
            @PathVariable("username") String username,
            @PathVariable("bookId") Long bookId) {

        reviewService.delete(username, bookId);
        return ResponseEntity.noContent().build();
    }
}
