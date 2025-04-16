package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;
import com.group8.busbookingbackend.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    @GetMapping("/route/{routeId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByRoute(@PathVariable String routeId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRoute(routeId);
        return ApiResponse.success(reviews, "Fetch route reviews successfully");
    }

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(
            @RequestHeader("User-Id") String userId, // Giả sử userId được gửi qua header
            @RequestBody ReviewRequest requestDto) {
        ReviewResponse responseDto = reviewService.createReview(userId, requestDto);
        return ApiResponse.success(responseDto, "Review created successfully");
    }
}
