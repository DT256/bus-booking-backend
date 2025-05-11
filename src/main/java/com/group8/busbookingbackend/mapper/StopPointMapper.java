package com.group8.busbookingbackend.mapper;

import com.group8.busbookingbackend.dto.stoppoint.StopPointDTO;
import com.group8.busbookingbackend.entity.StopPointEntity;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class StopPointMapper {
    public StopPointDTO toDTO(StopPointEntity entity) {
        if (entity == null) return null;
        
        return StopPointDTO.builder()
                .id(entity.getId().toString())
                .routeId(entity.getRouteId().toString())
                .locationId(entity.getLocationId().toString())
                .name(entity.getName())
                .address(entity.getAddress())
                .orderNumber(entity.getOrderNumber())
                .type(entity.getType())
                .estimatedTime(entity.getEstimatedTime())
                .build();
    }

    public StopPointEntity toEntity(StopPointDTO dto) {
        if (dto == null) return null;
        
        return StopPointEntity.builder()
                .id(dto.getId() != null ? new ObjectId(dto.getId()) : null)
                .routeId(new ObjectId(dto.getRouteId()))
                .locationId(new ObjectId(dto.getLocationId()))
                .name(dto.getName())
                .address(dto.getAddress())
                .orderNumber(dto.getOrderNumber())
                .type(dto.getType())
                .estimatedTime(dto.getEstimatedTime())
                .build();
    }
} 