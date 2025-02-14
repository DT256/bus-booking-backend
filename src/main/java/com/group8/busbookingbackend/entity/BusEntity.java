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
import java.util.ArrayList;
import java.util.List;

@Document(collection = "buses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusEntity {
    @Id
    public ObjectId id;
    public String licensePlate;
    public int capacity;
    private ObjectId categoryId;
    private List<String> imageUrls;
    private BusStatus status;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum BusStatus {
        ACTIVE, MAINTENANCE, INACTIVE
    }
}
