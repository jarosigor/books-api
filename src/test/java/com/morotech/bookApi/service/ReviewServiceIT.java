package com.morotech.bookApi.service;

import com.morotech.bookApi.config.TestcontainersConfig;
import com.morotech.bookApi.model.Review;
import com.morotech.bookApi.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestcontainersConfig.class)
@Transactional
class ReviewServiceIT {

    @Autowired
    ReviewService reviewService;

    @Autowired
    BookService bookService;

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("Test createReview - persists review and returns UUID")
    void createReview_persistsAndReturnsUuid() {
        var review = new Review()
                .bookId(99)
                .rating(5)
                .reviewText("Amazing book");

        var response = reviewService.createReview(review);

        assertThat(response.getReviewId()).isNotNull();

        var saved = reviewRepository.findAll().getFirst();

        assertThat(saved.getBookId()).isEqualTo(99);
        assertThat(saved.getRating()).isEqualTo(5);
        assertThat(saved.getReview()).isEqualTo("Amazing book");
    }

    @Test
    @DisplayName("Should complete full workflow: search book, get details, post review, verify review in details")
    void completeBookReviewWorkflow() {
        // Search for a book by title
        var searchResults = bookService.searchBooks("oliver", 1, 10);

        assertThat(searchResults).isNotNull();
        assertFalse(searchResults.getContent().isEmpty());

        // Get the first book's ID from search results
        var bookId = searchResults.getContent().getFirst().getBookId();

        // Get initial book details (should have no reviews yet)
        var initialDetails = bookService.getBookDetails(bookId);

        assertThat(initialDetails).isNotNull();
        assertThat(initialDetails.getBookId()).isEqualTo(bookId);
        var initialReviewCount = initialDetails.getReviews() != null ?
                initialDetails.getReviews().size() : 0;

        // Create a review for this book
        var newReview = new Review()
                .bookId(bookId)
                .rating(5)
                .reviewText("Excellent classic literature!");

        var reviewResponse = reviewService.createReview(newReview);

        assertThat(reviewResponse).isNotNull();
        assertThat(reviewResponse.getReviewId()).isNotNull();
        assertThat(reviewResponse.getBookId()).isEqualTo(bookId);
        assertThat(reviewResponse.getRating()).isEqualTo(5);

        // Get book details again and verify the review is included
        var updatedDetails = bookService.getBookDetails(bookId);

        assertThat(updatedDetails).isNotNull();
        assertThat(updatedDetails.getReviews().size()).isEqualTo(initialReviewCount + 1);
        assertTrue(updatedDetails.getReviews().contains("Excellent classic literature!"));

        // Verify average rating is calculated correctly
        if (initialReviewCount == 0) {
            assertThat(updatedDetails.getAverageRating()).isEqualTo(5.0);
        } else {
            assertThat(updatedDetails.getAverageRating()).isNotNull();
            assertThat(updatedDetails.getAverageRating()).isGreaterThan(0.0);
        }
    }

}
