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
import java.util.ArrayList;
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
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public BookingEntity bookTrip(ObjectId userId, ObjectId tripId, List<ObjectId> seatIds, BigDecimal totalPrice,
                                    BookingEntity.PickupDropoff pickupPoint, BookingEntity.PickupDropoff dropoffPoint,
                                  BookingEntity.PassengerDetail passengerDetails) {
        List<TripSeatEntity> seats = tripSeatRepository.findByTripIdAndStatus(tripId, TripSeatEntity.SeatStatus.AVAILABLE);
        if (seats.size() < seatIds.size()) {
            throw new IllegalStateException("Không đủ ghế trống cho chuyến đi này");
        }

        // Kiểm tra pickup/dropoff
        if (pickupPoint == null || dropoffPoint == null) {
            throw new IllegalArgumentException("Điểm đón và trả không được để trống");
        }
        if (pickupPoint.getTime().isAfter(dropoffPoint.getTime())) {
            throw new IllegalArgumentException("Thời gian đón phải trước thời gian trả");
        }

        String bookingCode = UUID.randomUUID().toString();

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
            tripSeatRepository.save(seat);
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
                .pickupPoint(pickupPoint)
                .dropoffPoint(dropoffPoint)
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
            bookingRepository.save(booking);

            for (ObjectId seatId : booking.getSeatIds()) {
                TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(booking.getTripId(), seatId);
                if (seat != null) {
                    seat.setStatus(TripSeatEntity.SeatStatus.AVAILABLE);
                    tripSeatRepository.save(seat);
                }
            }
        }
    }

    public BookingResponse cancelBooking(String bookingCode) {
        // Tìm booking theo bookingCode
        BookingEntity booking = bookingRepository.findByBookingCode(bookingCode).get();
        if (booking == null) {
            throw new IllegalArgumentException("Không tìm thấy đặt vé với mã: " + bookingCode);
        }

        // Kiểm tra trạng thái booking
        if (booking.getStatus() == BookingEntity.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Vé đã bị hủy trước đó");
        }
        if (booking.getStatus() == BookingEntity.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy vé đã hoàn thành");
        }

        // Kiểm tra thời gian khởi hành của chuyến đi
        TripEntity trip = tripRepository.findById(booking.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến đi: " + booking.getTripId()));
        if (LocalDateTime.now().isAfter(trip.getDepartureTime())) {
            throw new IllegalStateException("Không thể hủy vé sau khi chuyến đi đã khởi hành");
        }

        // Cập nhật trạng thái booking
        booking.setStatus(BookingEntity.BookingStatus.CANCELLED);
        if (booking.getPaymentStatus() == BookingEntity.PaymentStatus.PAID) {
            booking.setPaymentStatus(BookingEntity.PaymentStatus.REFUNDED); // Nếu đã thanh toán, đánh dấu hoàn tiền
        }

        // Giải phóng ghế
        List<ObjectId> seatIds = booking.getSeatIds();
        for (ObjectId seatId : seatIds) {
            String redisKey = REDIS_PREFIX + booking.getTripId() + ":" + seatId;
            redisTemplate.delete(redisKey); // Xóa dữ liệu tạm trong Redis (nếu có)

            TripSeatEntity seat = tripSeatRepository.findByTripIdAndSeatId(booking.getTripId(), seatId);
            if (seat != null) {
                seat.setStatus(TripSeatEntity.SeatStatus.AVAILABLE);
                tripSeatRepository.save(seat);
            }
        }

        // Lưu thay đổi
        return this.toBookingResponse(bookingRepository.save(booking));
    }

    private BookingResponse toBookingResponse(BookingEntity booking) {
        TripEntity trip = tripRepository.findById(booking.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến đi: " + booking.getTripId()));

        BusEntity bus = busRepository.findById(trip.getBusId()).get();

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId().toString());
        bookingResponse.setUserId(booking.getUserId().toString());
        bookingResponse.setBookingCode(booking.getBookingCode());
        bookingResponse.setTotalPrice(booking.getTotalPrice());
        bookingResponse.setStatus(booking.getStatus());
        bookingResponse.setCreatedAt(booking.getCreatedAt());
        bookingResponse.setDepartureTime(trip.getDepartureTime());
        bookingResponse.setSeats(booking.getSeatIds().size());

        // Lấy dữ liệu từ TripEntity
        AddressEntity startPoint = addressRepository.findById(new ObjectId(booking.getPickupPoint().getLocationId())).get();
        bookingResponse.setStartCity(startPoint.getCity());

        AddressEntity endPoint = addressRepository.findById(new ObjectId(booking.getDropoffPoint().getLocationId())).get();
        bookingResponse.setEndCity(endPoint.getCity());

        // Ảnh xe buýt (nếu có)
        bookingResponse.setBusImage(bus != null ? bus.getImageUrls().get(0) : null); // Nếu bus không null

        return bookingResponse;
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


}
