package com.group8.busbookingbackend.dto.booking.request;

import com.group8.busbookingbackend.entity.BookingEntity;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {
    private ObjectId userId;
    private ObjectId tripId;
    private List<ObjectId> seatIds;
    private BigDecimal totalPrice;
    private BookingEntity.PassengerDetail passengerDetail;
    private BookingEntity.PickupDropoff pickupPoint;
    private BookingEntity.PickupDropoff dropoffPoint;
}