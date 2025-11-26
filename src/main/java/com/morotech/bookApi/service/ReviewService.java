package com.morotech.bookApi.service;

import com.morotech.bookApi.exception.ReviewPersistanceException;
import com.morotech.bookApi.model.CreateReviewResponse;
import com.morotech.bookApi.model.Review;
import com.morotech.bookApi.model.entity.ReviewEntity;
import com.morotech.bookApi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public CreateReviewResponse createReview(Review review) {
        var newReview = new ReviewEntity(
                review.getBookId(),
                review.getRating(),
                review.getReviewText()
        );
        try {
            var saved = reviewRepository.save(newReview);
            log.info("Review persisted: {}", saved);
            return new CreateReviewResponse()
                    .reviewId(saved.getUuid())
                    .bookId(saved.getBookId())
                    .rating(saved.getRating())
                    .reviewText(saved.getReview());
        } catch (DataAccessException e) {
            log.error("Error saving review to database", e);
            throw new ReviewPersistanceException("Error saving review to database");
        }
    }



}
