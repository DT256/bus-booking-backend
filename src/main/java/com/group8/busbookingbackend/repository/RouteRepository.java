package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.RouteEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RouteRepository extends MongoRepository<RouteEntity, ObjectId> {
}
