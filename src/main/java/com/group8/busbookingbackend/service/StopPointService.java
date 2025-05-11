package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.stoppoint.StopPointDTO;
import com.group8.busbookingbackend.entity.StopPointEntity;
import com.group8.busbookingbackend.entity.TripEntity;
import com.group8.busbookingbackend.mapper.StopPointMapper;
import com.group8.busbookingbackend.repository.StopPointRepository;
import com.group8.busbookingbackend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StopPointService {
    private final StopPointRepository stopPointRepository;
    private final StopPointMapper stopPointMapper;
    private final TripRepository tripRepository;

    public List<StopPointDTO> getStopPointsByRouteId(String routeId) {
        List<StopPointEntity> stopPoints = stopPointRepository.findByRouteIdOrderByOrderNumberAsc(new ObjectId(routeId));
        return stopPoints.stream()
                .map(stopPointMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StopPointDTO> getPickupPointsByRouteId(String routeId) {
        List<StopPointEntity> stopPoints = stopPointRepository.findByRouteIdOrderByOrderNumberAsc(new ObjectId(routeId));
        return stopPoints.stream()
                .filter(point -> point.getType() == StopPointEntity.StopPointType.PICKUP)
                .map(stopPointMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StopPointDTO> getDropoffPointsByRouteId(String routeId) {
        List<StopPointEntity> stopPoints = stopPointRepository.findByRouteIdOrderByOrderNumberAsc(new ObjectId(routeId));
        return stopPoints.stream()
                .filter(point -> point.getType() == StopPointEntity.StopPointType.DROPOFF)
                .map(stopPointMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StopPointDTO> getStopPointsByTripId(String tripId) {
        TripEntity trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        List<StopPointEntity> stopPoints = stopPointRepository.findByRouteIdOrderByOrderNumberAsc(trip.getRouteId());
        return stopPoints.stream()
                .map(stopPointMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StopPointDTO> getPickupPointsByTripId(String tripId) {
        TripEntity trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        List<StopPointEntity> stopPoints = stopPointRepository.findByRouteIdOrderByOrderNumberAsc(trip.getRouteId());
        return stopPoints.stream()
                .filter(point -> point.getType() == StopPointEntity.StopPointType.PICKUP)
                .map(stopPointMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StopPointDTO> getDropoffPointsByTripId(String tripId) {
        TripEntity trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        
        List<StopPointEntity> stopPoints = stopPointRepository.findByRouteIdOrderByOrderNumberAsc(trip.getRouteId());
        return stopPoints.stream()
                .filter(point -> point.getType() == StopPointEntity.StopPointType.DROPOFF)
                .map(stopPointMapper::toDTO)
                .collect(Collectors.toList());
    }

    public StopPointDTO createStopPoint(StopPointDTO stopPointDTO) {
        StopPointEntity entity = stopPointMapper.toEntity(stopPointDTO);
        StopPointEntity savedEntity = stopPointRepository.save(entity);
        return stopPointMapper.toDTO(savedEntity);
    }

    public StopPointDTO updateStopPoint(String id, StopPointDTO stopPointDTO) {
        StopPointEntity existingEntity = stopPointRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("Stop point not found"));
        
        StopPointEntity updatedEntity = stopPointMapper.toEntity(stopPointDTO);
        updatedEntity.setId(existingEntity.getId());
        updatedEntity.setCreatedAt(existingEntity.getCreatedAt());
        
        StopPointEntity savedEntity = stopPointRepository.save(updatedEntity);
        return stopPointMapper.toDTO(savedEntity);
    }

    public void deleteStopPoint(String id) {
        stopPointRepository.deleteById(new ObjectId(id));
    }
} 