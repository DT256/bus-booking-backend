package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.StopPointEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopPointRepository extends MongoRepository<StopPointEntity, ObjectId> {
    List<StopPointEntity> findByRouteId(ObjectId routeId);
    List<StopPointEntity> findByRouteIdOrderByOrderNumberAsc(ObjectId routeId);
} 