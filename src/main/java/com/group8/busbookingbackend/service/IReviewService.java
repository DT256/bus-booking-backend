package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;

import java.util.List;

public interface IReviewService {
    public ReviewResponse createReview(String userId, ReviewRequest requestDto);
    public List<ReviewResponse> getReviewsByRoute(String routeId);
}
