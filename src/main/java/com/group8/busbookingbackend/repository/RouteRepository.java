package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.RouteEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends MongoRepository<RouteEntity, ObjectId> {
//    @Query("{ 'startPoint.city': ?0, 'endPoint.city': ?1, 'status': 'ACTIVE' }")
    @Query("{ 'startPoint.city': ?0, 'endPoint.city': ?1, 'status': 'ACTIVE' }")
    List<RouteEntity> findByStartPoint_cityAndEndPoint_city(ObjectId startCity, ObjectId endCity);

    List<RouteEntity> findByStartPointInAndEndPointInAndStatus(List<ObjectId> startPointIds,
                                                              List<ObjectId> endPointIds,
                                                              RouteEntity.RouteStatus status);    List<RouteEntity> findByStartPointAndEndPoint(ObjectId startPoint, ObjectId endPoint);
    List<RouteEntity> findByStartPointAndEndPointAndStatus(ObjectId startPoint, ObjectId endPoint, RouteEntity.RouteStatus status);
    List<RouteEntity> findByStartPointAndEndPointAndStatus(String startPoint, String endPoint, RouteEntity.RouteStatus status);
}
