package com.group8.busbookingbackend.dto.trip.response;

import com.group8.busbookingbackend.entity.BusEntity;
import com.group8.busbookingbackend.entity.RouteEntity;
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
    private BusEntity bus;
    private RouteEntity route;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private String startPointCity;
    private String endPointCity;
    private int availableSeats;
    private String busType;
    private String status;
}
