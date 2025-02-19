package com.group8.busbookingbackend.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Document(collection = "promotions")
public class PromotionEntity {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String code;

    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minimumBookingAmount;
    private BigDecimal maximumDiscountAmount;
    private Integer usageLimit;
    private Integer usageCount;
    private PromotionStatus status;
    private List<String> applicableRoutes;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    public enum PromotionStatus {
        ACTIVE, INACTIVE
    }
}
