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
import java.util.List;

@Document(collection = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripEntity {
    @Id
    private ObjectId id;
    private ObjectId busId;
    private ObjectId routeId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private TripStatus status;
    private List<ObjectId> seatIds;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum TripStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
