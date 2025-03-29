package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.booking.request.BookingRequest;
import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.SeatEntity;
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
    public ResponseEntity<BookingEntity> bookTrip(
            @RequestParam ObjectId userId,
            @RequestParam ObjectId tripId,
            @RequestParam List<ObjectId> seatIds,
            @RequestParam BigDecimal totalPrice) {
        try {


            BookingEntity booking = bookingService.bookTrip(userId, tripId, seatIds, totalPrice);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<BookingEntity> confirmPayment(@RequestParam String bookingCode) {
        try {
            BookingEntity booking = bookingService.confirmPayment(bookingCode);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/cancel/{bookingId}")
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable String bookingId) {
        BookingResponse response = bookingService.cancelBooking(bookingId);
        return ApiResponse.success(response,"Cancel booking successfully");
    }
}
