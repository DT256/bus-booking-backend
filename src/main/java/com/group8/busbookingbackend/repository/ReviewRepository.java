package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.AddressEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<ReviewRepository, ObjectId> {
    List<ReviewRepository> findByBookingId(ObjectId bookingId);
    List<ReviewRepository> findByUserId(ObjectId userId);
    List<ReviewRepository> findByRating(Integer rating);
}
