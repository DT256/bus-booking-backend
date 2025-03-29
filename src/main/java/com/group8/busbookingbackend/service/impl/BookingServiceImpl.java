package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.entity.*;
import com.group8.busbookingbackend.repository.*;
import com.group8.busbookingbackend.service.IBookingService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private TripSeatRepository tripSeatRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int PAYMENT_TIMEOUT_MINUTES = 30;
    private static final String REDIS_PREFIX = "seat:pending:";

    public BookingEntity bookTrip(ObjectId userId, ObjectId tripId, List<ObjectId> seatIds, BigDecimal totalPrice) {
        // Kiểm tra xem ghế có trống không
        List<TripSeatEntity> seats = tripSeatRepository.findByTripIdAndStatus(tripId, TripSeatEntity.SeatStatus.AVAILABLE);
        System.out.println(seats.size());
        if (seats.size() < seatIds.size()) {
            throw new IllegalStateException("Không đủ ghế trống cho chuyến đi này");
        }

        // Kiểm tra ghế có đang chờ thanh toán không
        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + tripId + ":" + seatId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                throw new IllegalStateException("Ghế " + seatId + " đang được giữ bởi người khác");
            }
        }

        // Tạo mã đặt vé duy nhất
        String bookingCode = UUID.randomUUID().toString();

        // Đặt ghế vào trạng thái tạm giữ trong Redis
        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + tripId + ":" + seatId;
            redisTemplate.opsForValue().set(redisKey, bookingCode, PAYMENT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            System.out.println(tripId);
            System.out.println(seatId);
            TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(tripId, seatId);
            System.out.println(seat);
            seat.setStatus(TripSeatEntity.SeatStatus.BOOKED);
            tripSeatRepository.save(seat);
        }

        // Tạo booking
        BookingEntity booking = BookingEntity.builder()
                .userId(userId)
                .tripId(tripId)
                .seatIds(seatIds)
                .totalPrice(totalPrice)
                .status(BookingEntity.BookingStatus.PENDING)
                .paymentStatus(BookingEntity.PaymentStatus.PENDING)
                .paymentMethod(BookingEntity.PaymentMethod.CASH) // Mặc định, có thể thay đổi
                .bookingCode(bookingCode)
                .createdAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public BookingEntity confirmPayment(String bookingCode) {
        BookingEntity booking = bookingRepository.findByBookingCode(bookingCode).get();
        if (booking == null) {
            throw new IllegalArgumentException("Không tìm thấy đặt vé với mã: " + bookingCode);
        }

        if (booking.getPaymentStatus() != BookingEntity.PaymentStatus.PENDING) {
            throw new IllegalStateException("Đặt vé đã được thanh toán hoặc hủy");
        }

        // Kiểm tra xem còn trong thời gian 30 phút không
        List<ObjectId> seatIds = booking.getSeatIds();
        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + booking.getTripId() + ":" + seatId;
            if (!redisTemplate.hasKey(redisKey)) {
                throw new IllegalStateException("Thời gian giữ ghế đã hết, vui lòng đặt lại");
            }
        }

        // Cập nhật trạng thái
        booking.setPaymentStatus(BookingEntity.PaymentStatus.PAID);
        booking.setStatus(BookingEntity.BookingStatus.CONFIRMED);

        // Xóa ghế khỏi Redis sau khi thanh toán thành công
        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + booking.getTripId() + ":" + seatId;
            redisTemplate.delete(redisKey);
        }

        return bookingRepository.save(booking);
    }

    // Hủy booking nếu quá thời gian
    @Override
    public void cancelExpiredBooking(String bookingCode) {
        BookingEntity booking = bookingRepository.findByBookingCode(bookingCode).get();
        if (booking != null && booking.getPaymentStatus() == BookingEntity.PaymentStatus.PENDING) {
            booking.setStatus(BookingEntity.BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // Cập nhật trạng thái ghế về AVAILABLE
            for (ObjectId seatId : booking.getSeatIds()) {
                TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(booking.getTripId(), seatId);
                seat.setStatus(TripSeatEntity.SeatStatus.AVAILABLE);
                tripSeatRepository.save(seat);
            }
        }
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
