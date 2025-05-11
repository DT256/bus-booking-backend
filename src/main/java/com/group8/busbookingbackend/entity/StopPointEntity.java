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

@Document(collection = "stop_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StopPointEntity {
    @Id
    private ObjectId id;
    private ObjectId routeId;
    private ObjectId locationId;
    private String name;
    private String address;
    private int orderNumber; // Thứ tự điểm dừng trong tuyến
    private StopPointType type; // Điểm đón hoặc điểm trả
    private LocalDateTime estimatedTime; // Thời gian dự kiến đến điểm dừng
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum StopPointType {
        PICKUP, DROPOFF
    }
} 