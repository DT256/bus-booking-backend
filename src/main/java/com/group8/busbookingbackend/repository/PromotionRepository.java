package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.PromotionEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends MongoRepository<PromotionEntity, ObjectId> {
    Optional<PromotionEntity> findByCode(String code);
    List<PromotionEntity> findByStatus(PromotionEntity.PromotionStatus status);
    List<PromotionEntity> findByStartDateBeforeAndEndDateAfterAndStatus(
            LocalDateTime now,
            LocalDateTime now2,
            PromotionEntity.PromotionStatus status
    );
}
