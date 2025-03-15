package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.SeatEntity;

import java.util.List;

public interface IBookingService {
    public BookingResponse bookTrip(String userId, String tripId, List<String> selectedSeatIds,
                                    List<BookingEntity.PassengerDetail> passengerDetails,
                                    BookingEntity.PickupDropoff pickupPoint,
                                    BookingEntity.PickupDropoff dropoffPoint);

    public BookingResponse cancelBooking(String bookingId);
    public List<SeatEntity> getAvailableSeats(String tripId);
}
