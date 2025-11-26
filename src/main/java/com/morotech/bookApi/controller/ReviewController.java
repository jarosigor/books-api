package com.morotech.bookApi.controller;

import com.morotech.bookApi.api.ReviewsApi;
import com.morotech.bookApi.model.CreateReviewResponse;
import com.morotech.bookApi.model.PostReviewRequestBody;
import com.morotech.bookApi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController implements ReviewsApi {

    private final ReviewService reviewService;

    @Override
    public ResponseEntity<CreateReviewResponse> createReview(PostReviewRequestBody postReviewRequestBody) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(postReviewRequestBody.getBody()));
    }
}
