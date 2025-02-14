package com.group8.busbookingbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteEntity{
    @Id
    private ObjectId id;
    private AddressEntity startPoint;
    private AddressEntity endPoint;
    private int distance;
    private double duration;
    private String description;
    private RouteStatus status;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    public enum RouteStatus {
        ACTIVE, INACTIVE
    }
}
