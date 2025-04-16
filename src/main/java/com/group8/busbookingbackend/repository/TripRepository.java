package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.TripEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends MongoRepository<TripEntity, ObjectId> {
    Optional<TripEntity> findById(ObjectId id);
    List<TripEntity> findByRouteId(ObjectId routeId);
    List<TripEntity> findByBusId(ObjectId busId);
    List<TripEntity> findByStatus(TripEntity.TripStatus status);
    List<TripEntity> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    List<TripEntity> findByRouteIdAndDepartureTimeBetween(ObjectId routeId, LocalDateTime departureTime, LocalDateTime departureTime2);

    @Query("{ 'routeId': ?0, 'departureTime': { $gte: ?1, $lt: ?2 }, 'status': 'SCHEDULED' }")
    List<TripEntity> findByRouteIdAndDepartureTimeAfter(ObjectId routeId, LocalDateTime start, LocalDateTime end);

    @Query("{ 'routeId': ?0, 'departureTime': { $gte: ?1, $lt: ?2 }, 'status': 'SCHEDULED' }")
    List<TripEntity> findByRouteIdAndDepartureTimeRange(ObjectId routeId, LocalDateTime start, LocalDateTime end);

}
