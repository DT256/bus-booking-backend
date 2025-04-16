package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.BusEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<BookingEntity, ObjectId> {
    Optional<BookingEntity> findByBookingCode(String bookingCode);
    Optional<BookingEntity> findById(String bookingCode);
    List<BookingEntity> findByUserId(ObjectId userId);
    @Query("{ 'tripId': { $in: ?0 } }")
    List<BookingEntity> findByTripIds(List<ObjectId> tripIds);
    List<BookingEntity> findByTripId(ObjectId tripId);
    List<BookingEntity> findByStatus(BookingEntity.BookingStatus status);
    List<BookingEntity> findByPaymentStatus(BookingEntity.PaymentStatus paymentStatus);

    @Aggregation(pipeline = {
            "{ '$lookup': { 'from': 'trips', 'localField': 'tripId', 'foreignField': '_id', 'as': 'tripDetails' } }",
            "{ '$unwind': '$tripDetails' }",
            "{ '$group': { '_id': '$tripDetails.busId', 'count': { '$sum': 1 } } }",
            "{ '$sort': { 'count': -1 } }",
            "{ '$limit': 10 }",
            "{ '$lookup': { 'from': 'buses', 'localField': '_id', 'foreignField': '_id', 'as': 'busDetails' } }",
            "{ '$unwind': '$busDetails' }"
    })
    List<BusEntity> findTop10BestSellers();
}
