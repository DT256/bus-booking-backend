package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.AddressEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepository extends MongoRepository<AddressEntity, ObjectId> {
    AddressEntity findByCity(String startCity);
}
