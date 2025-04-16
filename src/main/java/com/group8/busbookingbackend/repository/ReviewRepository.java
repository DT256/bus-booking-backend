package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.AddressEntity;
import com.group8.busbookingbackend.entity.ReviewEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<ReviewEntity, ObjectId> {
    Optional<ReviewEntity> findByBookingId(ObjectId id);
    @Query("{ 'bookingId': { $in: ?0 } }")
    List<ReviewEntity> findByBookingIds(List<ObjectId> bookingIds);
}
