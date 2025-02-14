package com.group8.busbookingbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bookings")
public class BookingEntity {
    @Id
    private ObjectId id;
    private ObjectId tripId;
    private ObjectId userId;
    private List<ObjectId> seatIds;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String bookingCode;
    private List<PassengerDetail> passengerDetails;
    private PickupDropoff pickupPoint;
    private PickupDropoff dropoffPoint;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    public static class PassengerDetail {
        private String fullName;
        private String phoneNumber;
        private String email;
    }

    @Data
    public static class PickupDropoff {
        private String locationId;
        private LocalDateTime time;
    }

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    public enum PaymentStatus {
        PENDING, PAID, REFUNDED
    }

    public enum PaymentMethod {
        CASH, CREDIT_CARD, BANK_TRANSFER, E_WALLET
    }
}