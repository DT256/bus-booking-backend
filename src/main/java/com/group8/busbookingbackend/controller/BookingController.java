package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.booking.request.BookingRequest;
import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.SeatEntity;
import com.group8.busbookingbackend.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    // Xem danh sách ghế còn trống của một chuyến xe
    @GetMapping("/seats/available/{tripId}")
    public ApiResponse<List<SeatEntity>> getAvailableSeats(@PathVariable String tripId) {
        return ApiResponse.success(bookingService.getAvailableSeats(tripId),"Fetching available seats successfully");
    }

    // Đặt vé với danh sách ghế được chọn
    @PostMapping("/book")
    public ApiResponse<BookingResponse> bookTrip(
            @RequestParam String userId,
            @RequestParam String tripId,
            @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.bookTrip(userId, tripId, request.getSeatIds(),
                request.getPassengerDetails(), request.getPickupPoint(), request.getDropoffPoint());
        return ApiResponse.success(response,"Booking successfully");
    }

    @PostMapping("/cancel/{bookingId}")
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable String bookingId) {
        BookingResponse response = bookingService.cancelBooking(bookingId);
        return ApiResponse.success(response,"Cancel booking successfully");
    }
}
