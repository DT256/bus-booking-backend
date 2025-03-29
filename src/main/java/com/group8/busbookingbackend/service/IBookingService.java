package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.SeatEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;

public interface IBookingService {
    public BookingEntity bookTrip(ObjectId userId, ObjectId tripId, List<ObjectId> seatIds, BigDecimal totalPrice);
    public void cancelExpiredBooking(String bookingCode);
    public BookingEntity confirmPayment(String bookingCode);
    public BookingResponse cancelBooking(String bookingId);
    public List<SeatEntity> getAvailableSeats(String tripId);
}
