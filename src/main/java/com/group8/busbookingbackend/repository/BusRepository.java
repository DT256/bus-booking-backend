package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.BusEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BusRepository extends MongoRepository<BusEntity, ObjectId> {
    List<BusEntity> findByStatus(BusEntity.BusStatus status);
    List<BusEntity> findByCategoryId(ObjectId category_id);
    BusEntity findByLicensePlate(String licensePlate);

    @Aggregation(pipeline = {
            "{ '$match': { 'createdAt': { '$gte': ?0 } } }",
            "{ '$sort': { 'createdAt': -1 } }",
            "{ '$limit': 10 }"
    })
    List<BusEntity> findRecentBus(LocalDateTime date);
}
