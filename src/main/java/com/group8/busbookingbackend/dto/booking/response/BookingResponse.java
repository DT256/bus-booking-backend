package com.group8.busbookingbackend.dto.booking.response;

import com.group8.busbookingbackend.entity.BookingEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse{
    private String id;
    private String tripId;
    private String userId;
    private List<String> seatIds;
    private BigDecimal totalPrice;
    private BookingEntity.BookingStatus status;
    private BookingEntity.PaymentStatus paymentStatus;
    private BookingEntity.PaymentMethod paymentMethod;
    private String bookingCode;
    private List<BookingEntity.PassengerDetail> passengerDetails;
    private BookingEntity.PickupDropoff pickupPoint;
    private BookingEntity.PickupDropoff dropoffPoint;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
