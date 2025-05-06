package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.PaymentEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository  extends MongoRepository<PaymentEntity, ObjectId> {
}
