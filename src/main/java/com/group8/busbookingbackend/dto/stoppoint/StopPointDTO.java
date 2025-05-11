package com.group8.busbookingbackend.dto.stoppoint;

import com.group8.busbookingbackend.entity.StopPointEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopPointDTO {
    private String id;
    private String routeId;
    private String locationId;
    private String name;
    private String address;
    private int orderNumber;
    private StopPointEntity.StopPointType type;
    private LocalDateTime estimatedTime;
} 