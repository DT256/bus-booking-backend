package com.group8.busbookingbackend.dto.trip.response;

import com.group8.busbookingbackend.entity.AddressEntity;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TripDetailsResponse {
    private String id;
    private String busId;
    private String licensePlate;
    private int capacity;
    private LocalDateTime departureTime;
    private double price;
    private String status;
    private String startPointCity;
    private String endPointCity;
    private List<SeatDetails> seats;

    @Data
    public static class SeatDetails {
        private String id;
        private String seatId;
        private String seatNumber;
        private Integer floor;
        private String status;
    }
}
