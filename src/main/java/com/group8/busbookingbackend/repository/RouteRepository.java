package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.RouteEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RouteRepository extends MongoRepository<RouteEntity, ObjectId> {
    @Query("{ 'startPoint.city': ?0, 'endPoint.city': ?1, 'status': 'ACTIVE' }")
    List<RouteEntity> findByStartPoint_CityAndEndPoint_City(String startCity, String endCity);
}
