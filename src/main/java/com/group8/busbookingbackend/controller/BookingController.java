package com.group8.busbookingbackend.controller;

import com.cloudinary.Api;
import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.booking.request.BookingRequest;
import com.group8.busbookingbackend.dto.booking.response.BookingDetailResponse;
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
import java.util.stream.Collectors;

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
    public ApiResponse<BookingResponse> bookTrip(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestBody BookingRequest request) {
        System.out.println(authorizationHeader);
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);

        ObjectId userId = new ObjectId(strUserId);
        System.out.println(userId);
        ObjectId tripId = new ObjectId(request.getTripId());
        List<ObjectId> seatIds = request.getSeatIds().stream()
                .map(ObjectId::new)
                .collect(Collectors.toList());
        ObjectId pickupStopPointId = new ObjectId(request.getPickupStopPointId());
        ObjectId dropoffStopPointId = new ObjectId(request.getDropoffStopPointId());

        return ApiResponse.success(bookingService.bookTrip(
                userId,
                tripId,
                seatIds,
                request.getTotalPrice(),
                pickupStopPointId,
                dropoffStopPointId,
                request.getPassengerDetail()),
                "Create booking successfully");
    }

    @PostMapping("/confirm-payment")
    public ApiResponse<BookingEntity> confirmPayment(@RequestParam String bookingCode) {
        return ApiResponse.success(bookingService.confirmPayment(bookingCode), "Confirm payment successfully");
    }

    @PostMapping("/cancel")
    public ApiResponse<BookingResponse> cancelBooking(@RequestParam String bookingCode) {
        return ApiResponse.success(bookingService.cancelBooking(bookingCode), "Cancel booking successfully");
    }

    @GetMapping("/history")
    public ApiResponse<List<BookingResponse>> getBookingHistory(@RequestHeader("Authorization") String authorizationHeader) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);
        return ApiResponse.success(bookingService.getBookingHistory(userId), "Fetching booking history successfully");
    }
}
