package com.group8.busbookingbackend.dto.trip.response;

import com.group8.busbookingbackend.entity.AddressEntity;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TripDetailsResponse {
    private String id;
    private BusDetails bus;
    private RouteDetails route;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private String status;
    private List<SeatDetails> seats;

    @Data
    public static class BusDetails {
        private String id;
        private String licensePlate;
        private int capacity;
        private String categoryId;
        private String status;
    }

    @Data
    public static class RouteDetails {
        private String id;
        private AddressEntity startAddress;
        private AddressEntity endAddress;
        private int distance;
        private double duration;
        private String description;
        private String status;
    }

    @Data
    public static class SeatDetails {
        private String id;
        private String seatId;
        private String seatNumber;
        private Integer floor;
        private String status;
    }
}
