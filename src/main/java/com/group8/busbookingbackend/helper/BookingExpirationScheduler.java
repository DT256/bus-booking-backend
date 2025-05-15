package com.group8.busbookingbackend.helper;

import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class BookingExpirationScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IBookingService bookingService;

    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    public void checkExpiredBookings() {
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        List<BookingEntity> pendingBooking = bookingRepository.findAll();
        List<BookingEntity> pendingBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getPaymentStatus() == BookingEntity.PaymentStatus.PENDING)
                .filter(b -> b.getCreatedAt()
                        .plusMinutes(30)
                        .isBefore(LocalDateTime.now()))
                .toList();
        System.out.println("Current time: {}"+ now);
        for (BookingEntity booking : pendingBookings) {
            bookingService.cancelExpiredBooking(booking.getBookingCode());
        }
    }

}