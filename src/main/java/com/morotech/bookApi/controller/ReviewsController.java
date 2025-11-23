package com.morotech.bookApi.controller;

import com.morotech.bookApi.api.ReviewsApi;
import com.morotech.bookApi.model.PostReviewRequestBody;
import com.morotech.bookApi.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewsController implements ReviewsApi {

    @Override
    public ResponseEntity<Review> createReview(PostReviewRequestBody postReviewRequestBody) {
        return null;
    }
}
