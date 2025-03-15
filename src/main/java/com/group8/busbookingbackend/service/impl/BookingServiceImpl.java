package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.SeatEntity;
import com.group8.busbookingbackend.entity.TripEntity;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.repository.SeatRepository;
import com.group8.busbookingbackend.repository.TripRepository;
import com.group8.busbookingbackend.repository.UserRepository;
import com.group8.busbookingbackend.service.IBookingService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements IBookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    public BookingResponse bookTrip(String userId, String tripId, List<String> selectedSeatIds,
                                    List<BookingEntity.PassengerDetail> passengerDetails,
                                    BookingEntity.PickupDropoff pickupPoint,
                                    BookingEntity.PickupDropoff dropoffPoint) {
        // Kiểm tra trip
        TripEntity trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Kiểm tra user
        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra ghế còn trống
        List<SeatEntity> availableSeats = seatRepository.findByBusIdAndStatus(trip.getBusId(),
                SeatEntity.SeatStatus.AVAILABLE);
        List<ObjectId> seatIds = selectedSeatIds.stream().map(ObjectId::new).collect(Collectors.toList());

        if (availableSeats.size() < seatIds.size()) {
            throw new RuntimeException("Not enough available seats");
        }

        // Kiểm tra các ghế được chọn có hợp lệ không
        List<SeatEntity> seatsToBook = availableSeats.stream()
                .filter(seat -> seatIds.contains(seat.getId()))
                .collect(Collectors.toList());

        if (seatsToBook.size() != seatIds.size()) {
            throw new RuntimeException("Some selected seats are not available");
        }

        // Tạo booking
        BookingEntity booking = BookingEntity.builder()
                .tripId(trip.getId())
                .userId(user.getId())
                .seatIds(seatIds)
                .totalPrice(BigDecimal.valueOf(trip.getPrice() * seatIds.size()))
                .status(BookingEntity.BookingStatus.PENDING)
                .paymentStatus(BookingEntity.PaymentStatus.PENDING)
                .paymentMethod(BookingEntity.PaymentMethod.CASH) // Mặc định
                .bookingCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .passengerDetails(passengerDetails)
                .pickupPoint(pickupPoint)
                .dropoffPoint(dropoffPoint)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Cập nhật trạng thái ghế
        seatsToBook.forEach(seat -> {
            seat.setStatus(SeatEntity.SeatStatus.BOOKED);
            seat.setUpdatedAt(LocalDateTime.now());
            seatRepository.save(seat);
        });

        // Thêm booking vào danh sách tickets của user
        user.getTickets().add(booking.getId());
        userRepository.save(user);

        // Lưu booking
        booking = bookingRepository.save(booking);

        // Chuyển sang DTO để trả về
        return toBookingResponse(booking);
    }

    public BookingResponse cancelBooking(String bookingId) {
        // Tìm booking
        BookingEntity booking = bookingRepository.findById(new ObjectId(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra trạng thái booking
        if (booking.getStatus() == BookingEntity.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingEntity.BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed trip");
        }

        // Tìm trip
        TripEntity trip = tripRepository.findById(booking.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Kiểm tra thời gian hủy (ví dụ: không cho hủy nếu chuyến xe đã khởi hành)
        if (trip.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel after trip has started");
        }

        // Giải phóng ghế
        List<SeatEntity> bookedSeats = seatRepository.findAllById(booking.getSeatIds());
        for (SeatEntity seat : bookedSeats) {
            if (seat.getStatus() == SeatEntity.SeatStatus.BOOKED) {
                seat.setStatus(SeatEntity.SeatStatus.AVAILABLE);
                seat.setUpdatedAt(LocalDateTime.now());
                seatRepository.save(seat);
            }
        }

        // Cập nhật trạng thái booking
        booking.setStatus(BookingEntity.BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == BookingEntity.PaymentStatus.PAID) {
            booking.setPaymentStatus(BookingEntity.PaymentStatus.REFUNDED); // Giả định hoàn tiền
        }
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Xóa booking khỏi danh sách tickets của user
        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getTickets().remove(booking.getId());
        userRepository.save(user);

        // Chuyển sang DTO để trả về
        return toBookingResponse(booking);
    }

    private BookingResponse toBookingResponse(BookingEntity booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId().toString());
        bookingResponse.setTripId(booking.getTripId().toString());
        bookingResponse.setUserId(booking.getUserId().toString());
        bookingResponse.setSeatIds(booking.getSeatIds().stream().map(ObjectId::toString).collect(Collectors.toList()));
        bookingResponse.setTotalPrice(booking.getTotalPrice());
        bookingResponse.setStatus(booking.getStatus());
        bookingResponse.setPaymentStatus(booking.getPaymentStatus());
        bookingResponse.setPaymentMethod(booking.getPaymentMethod());
        bookingResponse.setBookingCode(booking.getBookingCode());
        bookingResponse.setPassengerDetails(booking.getPassengerDetails());
        bookingResponse.setPickupPoint(booking.getPickupPoint());
        bookingResponse.setDropoffPoint(booking.getDropoffPoint());
        bookingResponse.setCreatedAt(booking.getCreatedAt());
        bookingResponse.setUpdatedAt(booking.getUpdatedAt());

        return bookingResponse;
    }


    // Lấy danh sách ghế còn trống của chuyến xe
    public List<SeatEntity> getAvailableSeats(String tripId) {
        TripEntity trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return seatRepository.findByBusIdAndStatus(trip.getBusId(), SeatEntity.SeatStatus.AVAILABLE);
    }



}
