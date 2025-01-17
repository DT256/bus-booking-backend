package com.ducthang.busbookingbackend.repository;

import com.ducthang.busbookingbackend.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

}
