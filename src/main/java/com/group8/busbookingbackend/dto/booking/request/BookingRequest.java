package com.group8.busbookingbackend.dto.booking.request;

import com.group8.busbookingbackend.entity.BookingEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookingRequest {
    private String tripId;
    private List<String> seatIds;
    private BigDecimal totalPrice;
    private BookingEntity.PassengerDetail passengerDetail;
    private String pickupStopPointId;
    private String dropoffStopPointId;
}