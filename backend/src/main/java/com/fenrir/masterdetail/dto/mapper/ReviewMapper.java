package com.fenrir.masterdetail.dto.mapper;

import com.fenrir.masterdetail.dto.ReviewRequestDTO;
import com.fenrir.masterdetail.dto.StatisticsDTO;
import com.fenrir.masterdetail.model.Book;
import com.fenrir.masterdetail.model.Review;
import com.fenrir.masterdetail.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ReviewMapper {
    public Review fromReviewRequestDTO(ReviewRequestDTO dto, User user, Book book) {
        return new Review(
                new Review.Id(user.getId(), book.getId()),
                dto.getContent(),
                dto.getRate()
        );
    }

    public StatisticsDTO toStatisticsDTO(Long bookId, List<Review> shelves) {
        long numberOfRates = shelves.size();
        long numberOfComments = shelves.stream()
                .filter(shelf -> shelf.getContent() != null)
                .count();
        double rate = shelves.stream()
                .mapToInt(Review::getRate)
                .average()
                .orElse(0);
        rate = BigDecimal.valueOf(rate)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        return new StatisticsDTO(
                bookId,
                numberOfRates,
                numberOfComments,
                rate
        );
    }

}
