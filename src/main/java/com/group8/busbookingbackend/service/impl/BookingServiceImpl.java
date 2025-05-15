package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.dto.booking.response.BookingDetailResponse;
import com.group8.busbookingbackend.entity.*;
import com.group8.busbookingbackend.repository.*;
import com.group8.busbookingbackend.service.IBookingService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private StopPointRepository stopPointRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Override
    public BookingResponse bookTrip(ObjectId userId, ObjectId tripId, List<ObjectId> seatIds, BigDecimal totalPrice,
                                  ObjectId pickupPoint, ObjectId dropoffPoint,
                                  BookingEntity.PassengerDetail passengerDetails) {
        List<TripSeatEntity> seats = tripSeatRepository.findByTripIdAndStatus(tripId, TripSeatEntity.SeatStatus.AVAILABLE);
        if (seats.size() < seatIds.size()) {
            throw new IllegalStateException("Không đủ ghế trống cho chuyến đi này");
        }

        String bookingCode = generateUniqueBookingCode(6);

        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + tripId + ":" + seatId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                throw new IllegalStateException("Ghế " + seatId + " đang được giữ bởi người khác");
            }
            redisTemplate.opsForValue().set(redisKey, bookingCode, PAYMENT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(tripId, seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Không tìm thấy ghế với tripId: " + tripId + " và seatId: " + seatId);
            }
            seat.setStatus(TripSeatEntity.SeatStatus.BOOKED);
            TripSeatEntity seatsau = tripSeatRepository.save(seat);
            System.out.println(seatsau);
        }

        BookingEntity booking = BookingEntity.builder()
                .userId(userId)
                .tripId(tripId)
                .seatIds(seatIds)
                .totalPrice(totalPrice)
                .status(BookingEntity.BookingStatus.PENDING)
                .paymentStatus(BookingEntity.PaymentStatus.PENDING)
                .paymentMethod(BookingEntity.PaymentMethod.CASH)
                .passengerDetail(passengerDetails)
                .pickupStopPointId(pickupPoint)
                .dropoffStopPointId(dropoffPoint)
                .bookingCode(bookingCode)
                .createdAt(LocalDateTime.now())
                .build();

        return toBookingResponse(bookingRepository.save(booking));
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

        List<ObjectId> seatIds = booking.getSeatIds();
        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + booking.getTripId() + ":" + seatId;
            if (!redisTemplate.hasKey(redisKey)) {
                throw new IllegalStateException("Thời gian giữ ghế đã hết, vui lòng đặt lại");
            }
        }

        booking.setPaymentStatus(BookingEntity.PaymentStatus.PAID);
        booking.setStatus(BookingEntity.BookingStatus.CONFIRMED);

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
            booking.setPaymentStatus(BookingEntity.PaymentStatus.REFUNDED);
            bookingRepository.save(booking);

            for (ObjectId seatId : booking.getSeatIds()) {
                TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(booking.getTripId(), seatId);
                if (seat != null) {
                    seat.setStatus(TripSeatEntity.SeatStatus.AVAILABLE);
                    System.out.println("o cancal " + seat.getId());
                    tripSeatRepository.save(seat);
                }
            }
        }
    }

    @Override
    public BookingResponse cancelBooking(String bookingCode) {
        // 1. Tìm booking theo bookingCode
        BookingEntity booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vé với mã: " + bookingCode));

        // 2. Kiểm tra trạng thái vé
        if (booking.getStatus() == BookingEntity.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Vé đã bị hủy trước đó");
        }
        if (booking.getStatus() == BookingEntity.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy vé vì vé đã hoàn thành");
        }

        // 3. Kiểm tra thời gian khởi hành
        TripEntity trip = tripRepository.findById(booking.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến đi: " + booking.getTripId()));
        if (LocalDateTime.now().isAfter(trip.getDepartureTime())) {
            throw new IllegalStateException("Không thể hủy vé sau khi chuyến đi đã khởi hành");
        }

        // 4. Cập nhật trạng thái vé
        booking.setStatus(BookingEntity.BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == BookingEntity.PaymentStatus.PAID) {
            booking.setPaymentStatus(BookingEntity.PaymentStatus.REFUNDED);
        }

        // 5. Giải phóng ghế đã giữ
        for (ObjectId seatId : booking.getSeatIds()) {
            String redisKey = REDIS_PREFIX + booking.getTripId() + ":" + seatId;
            redisTemplate.delete(redisKey);

            TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(booking.getTripId(), seatId);
            if (seat != null && seat.getStatus() != TripSeatEntity.SeatStatus.AVAILABLE) {
                seat.setStatus(TripSeatEntity.SeatStatus.AVAILABLE);
                tripSeatRepository.save(seat);
            }
        }

        // 7. Lưu thay đổi & trả kết quả
        return toBookingResponse(bookingRepository.save(booking));
    }



    private BookingResponse toBookingResponse(BookingEntity booking) {
        TripEntity trip = tripRepository.findById(booking.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến đi: " + booking.getTripId()));

        RouteEntity route = routeRepository.findById(trip.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tuyến đường: " + trip.getRouteId()));

        BusEntity bus = busRepository.findById(trip.getBusId()).get();

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId().toString());
        bookingResponse.setUserId(booking.getUserId().toString());
        bookingResponse.setBookingCode(booking.getBookingCode());
        bookingResponse.setTotalPrice(booking.getTotalPrice());
        bookingResponse.setStatus(booking.getStatus());
        bookingResponse.setPaymentStatus(booking.getPaymentStatus());
        bookingResponse.setCreatedAt(booking.getCreatedAt());
        bookingResponse.setDepartureTime(trip.getDepartureTime());
        bookingResponse.setArrivalTime(trip.getArrivalTime());
        bookingResponse.setSeats(booking.getSeatIds().size());

        List<String> seatNumbers = new ArrayList<>();
        for (ObjectId seatId : booking.getSeatIds()) {
            SeatEntity seat = seatRepository.findById(seatId).orElse(null);
            if (seat != null) {
                seatNumbers.add(seat.getSeatNumber());
            }
        }
        bookingResponse.setSeatNames(seatNumbers);

        // Set start and end cities from stop points
        bookingResponse.setStartCity(addressRepository.findById(route.getStartPoint()).get().getCity());
        bookingResponse.setEndCity(addressRepository.findById(route.getEndPoint()).get().getCity());

        // Set bus image
        bookingResponse.setBusImage(bus != null ? bus.getImageUrls().get(0) : null);

        return bookingResponse;
    }

    private BookingDetailResponse toBookingDetailResponse(BookingEntity booking) {
        BookingDetailResponse response = new BookingDetailResponse();
        response.setId(booking.getId().toString());
        response.setTripId(booking.getTripId().toString());
        response.setUserId(booking.getUserId().toString());
        response.setSeatIds(booking.getSeatIds().stream().map(ObjectId::toString).toList());
        response.setTotalPrice(booking.getTotalPrice());
        response.setStatus(booking.getStatus());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setPaymentMethod(booking.getPaymentMethod());
        response.setBookingCode(booking.getBookingCode());
        response.setPassengerDetail(booking.getPassengerDetail());
        response.setPickupStopPointId(booking.getPickupStopPointId().toString());
        response.setDropoffStopPointId(booking.getDropoffStopPointId().toString());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        return response;
    }

    // Lấy danh sách ghế còn trống của chuyến xe
    public List<SeatEntity> getAvailableSeats(String tripId) {
        TripEntity trip = tripRepository.findById(new ObjectId(tripId))
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return seatRepository.findByBusIdAndStatus(trip.getBusId(), SeatEntity.SeatStatus.AVAILABLE);
    }

    @Override
    public List<BookingResponse> getBookingHistory(ObjectId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được để trống");
        }

        List<BookingEntity> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy lịch sử đặt vé cho người dùng này");
        }

        return bookings.stream().map(this::toBookingResponse).toList();
    }

    private String generateUniqueBookingCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new SecureRandom();

        String code;
        do {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = builder.toString();
        } while (bookingRepository.existsByBookingCode(code));

        return code;
    }


}
