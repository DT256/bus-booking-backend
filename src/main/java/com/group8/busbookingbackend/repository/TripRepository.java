package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.TripEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends MongoRepository<TripEntity, ObjectId> {
    List<TripEntity> findByRouteId(ObjectId routeId);
    List<TripEntity> findByBusId(ObjectId busId);
    List<TripEntity> findByStatus(TripEntity.TripStatus status);
    List<TripEntity> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    List<TripEntity> findByRouteIdAndDepartureTimeBetween(ObjectId routeId, LocalDateTime departureTime, LocalDateTime departureTime2);
}
