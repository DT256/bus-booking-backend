package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;
import com.group8.busbookingbackend.security.JwtProvider;
import com.group8.busbookingbackend.service.IReviewService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
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

    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<ReviewResponse> createReview(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("data") ReviewRequest requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);

        // Gán danh sách ảnh vào requestDto
        requestDto.setImages(images != null ? images : Collections.emptyList());

        ReviewResponse responseDto = reviewService.createReview(userId, requestDto);
        return ApiResponse.success(responseDto, "Review created successfully");
    }

}
