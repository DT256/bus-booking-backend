package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.CategoryEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<CategoryEntity, ObjectId> {
}
