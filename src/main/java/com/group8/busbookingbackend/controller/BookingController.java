package com.group8.busbookingbackend.controller;

import com.cloudinary.Api;
import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.booking.request.BookingRequest;
import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.SeatEntity;
import com.group8.busbookingbackend.security.JwtProvider;
import com.group8.busbookingbackend.service.IBookingService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping("/book")
    public ApiResponse<BookingEntity> bookTrip(@RequestBody BookingRequest request) {
        return ApiResponse.success(bookingService.bookTrip(request.getUserId(),
                        request.getTripId(),
                        request.getSeatIds(),
                        request.getTotalPrice(),
                        request.getPickupPoint(),
                        request.getDropoffPoint(),
                        request.getPassengerDetail()),
                "Create booking successfully");
    }

    @PostMapping("/confirm-payment")
    public ApiResponse<BookingEntity> confirmPayment(@RequestParam String bookingCode) {
        return ApiResponse.success(bookingService.confirmPayment(bookingCode), "Confirm payment successfully");
    }

    @PostMapping("/cancel")
    public ApiResponse<BookingEntity> cancelBooking(@RequestParam String bookingCode) {
        return ApiResponse.success(bookingService.cancelBooking(bookingCode), "Cancel booking successfully");
    }

    @GetMapping("/history")
    public ApiResponse<List<BookingResponse>> getBookingHistory(@RequestHeader("Authorization") String authorizationHeader) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);
        return ApiResponse.success(bookingService.getBookingHistory(userId), "Fetching booking history successfully");
    }
}
