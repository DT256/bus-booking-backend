package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;
import org.bson.types.ObjectId;

import java.util.List;

public interface IReviewService {
    public ReviewResponse createReview(ObjectId userId, ReviewRequest requestDto);
    public List<ReviewResponse> getReviewsByRoute(String routeId);
}
