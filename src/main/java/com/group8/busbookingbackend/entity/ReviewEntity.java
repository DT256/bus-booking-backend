package com.group8.busbookingbackend.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "reviews")
public class ReviewEntity {
    @Id
    private ObjectId id;
    private ObjectId bookingId;
    private ObjectId userId;
    private Integer rating;
    private String comment;
    private List<String> imageUrls;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
