package com.group8.busbookingbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Document(collection = "trip_seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSeatEntity {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId tripId; // Chuyến đi nào
    @Indexed
    private ObjectId seatId; // Ghế nào
    private SeatStatus status; // Trạng thái của ghế theo chuyến này

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum SeatStatus {
        AVAILABLE, BOOKED, MAINTENANCE
    }
}

