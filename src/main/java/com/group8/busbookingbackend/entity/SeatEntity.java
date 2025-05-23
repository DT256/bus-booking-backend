package com.group8.busbookingbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatEntity {
    @Id
    private ObjectId id;
    private String seatNumber;
    @Indexed
    private ObjectId busId;
    private Integer floor;
    private SeatType type;
    private SeatStatus status;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum SeatType {
        SLEEPER, SEATER
    }

    public enum SeatStatus {
        AVAILABLE, BOOKED, MAINTENANCE
    }
}
