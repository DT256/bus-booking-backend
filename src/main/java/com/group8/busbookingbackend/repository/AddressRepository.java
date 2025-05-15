package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.AddressEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends MongoRepository<AddressEntity, ObjectId> {
//    @Query(value = "{ 'city' : { $regex: ?0, $options: 'i' } }")
//    AddressEntity findByCity(String city);


    @Query(value = "{ 'city' : { $regex: ?0, $options: 'i' } }")
    List<AddressEntity> findByCity(String city);
}
