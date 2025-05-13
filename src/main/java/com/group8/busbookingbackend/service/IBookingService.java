package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.booking.response.BookingDetailResponse;
import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.SeatEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;

public interface IBookingService {
    public BookingResponse bookTrip(ObjectId userId, ObjectId tripId, List<ObjectId> seatIds, BigDecimal totalPrice,
                                          ObjectId pickupStopPointId, ObjectId dropoffStopPointId,
                                          BookingEntity.PassengerDetail passengerDetails);
    public void cancelExpiredBooking(String bookingCode);
    public BookingEntity confirmPayment(String bookingCode);
    public BookingResponse cancelBooking(String bookingCode);
    public List<SeatEntity> getAvailableSeats(String tripId);
    public List<BookingResponse> getBookingHistory(ObjectId userId);
}
