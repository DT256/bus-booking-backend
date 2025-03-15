package com.group8.busbookingbackend.dto.trip.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TripSearchResponse {
    private String id;
    private String busId;
    private String routeId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private String startPointCity;
    private String endPointCity;
    private int availableSeats;
    private String busType;
    private String status;
}
