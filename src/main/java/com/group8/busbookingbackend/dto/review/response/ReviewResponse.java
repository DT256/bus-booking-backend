package com.group8.busbookingbackend.dto.review.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponse {
    private String id;
    private String bookingId;
    private String userId;
    private Integer rating;
    private String comment;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
