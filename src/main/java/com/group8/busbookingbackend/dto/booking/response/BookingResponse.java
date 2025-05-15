package com.group8.busbookingbackend.dto.booking.response;

import com.group8.busbookingbackend.entity.BookingEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {
    private String id;
    private String userId;
    private String busImage;
    private String startCity;
    private String endCity;
    private int seats;
    private List<String> seatNames;
    private BigDecimal totalPrice;
    private BookingEntity.BookingStatus status;
    private BookingEntity.PaymentStatus paymentStatus;
    private String bookingCode;
    private LocalDateTime createdAt;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

}
