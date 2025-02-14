package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.BookingEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<BookingEntity, ObjectId> {
    Optional<BookingEntity> findByBookingCode(String bookingCode);
    List<BookingEntity> findByUserId(ObjectId userId);
    List<BookingEntity> findByTripId(ObjectId tripId);
    List<BookingEntity> findByStatus(BookingEntity.BookingStatus status);
    List<BookingEntity> findByPaymentStatus(BookingEntity.PaymentStatus paymentStatus);
}
