package com.group8.busbookingbackend.dto.review.request;

import lombok.Data;
import java.util.List;

@Data
public class ReviewRequest {
    private String bookingId;
    private Integer rating;
    private String comment;
    private List<String> imageUrls;
}
