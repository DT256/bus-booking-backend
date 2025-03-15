package com.group8.busbookingbackend.dto.booking.request;

import com.group8.busbookingbackend.entity.BookingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private List<String> seatIds;
    private List<BookingEntity.PassengerDetail> passengerDetails;
    private BookingEntity.PickupDropoff pickupPoint;
    private BookingEntity.PickupDropoff dropoffPoint;
}
