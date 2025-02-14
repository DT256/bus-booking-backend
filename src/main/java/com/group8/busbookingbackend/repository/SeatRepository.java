package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.SeatEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeatRepository extends MongoRepository<SeatEntity, ObjectId> {
    List<SeatEntity> findByBusId(ObjectId busId);
    List<SeatEntity> findByBusIdAndStatus(ObjectId busId, SeatEntity.SeatStatus status);
    List<SeatEntity> findByBusIdAndFloor(ObjectId busId, Integer floor);
}
