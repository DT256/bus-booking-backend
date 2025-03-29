package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.TripSeatEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripSeatRepository extends MongoRepository<TripSeatEntity, ObjectId> {

    @Query("{ 'tripId': ?0, 'status': 'AVAILABLE' }")
    List<TripSeatEntity> findAvailableSeatsByTrip(ObjectId tripId);

    List<TripSeatEntity> findByTripId(ObjectId tripId);

    List<TripSeatEntity> findByTripIdAndStatus(ObjectId tripId, TripSeatEntity.SeatStatus status);
    TripSeatEntity findByTripIdAndSeatId(ObjectId tripId, ObjectId seatId);
}

