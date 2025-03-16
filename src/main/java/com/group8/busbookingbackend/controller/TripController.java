package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.trip.request.TripSearchRequest;
import com.group8.busbookingbackend.dto.trip.response.TripDetailsResponse;
import com.group8.busbookingbackend.dto.trip.response.TripSearchResponse;
import com.group8.busbookingbackend.service.ITripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private ITripService tripService;

    // Tìm kiếm chuyến đi
    @PostMapping("/search")
    public ApiResponse<List<TripSearchResponse>> searchTrips(@RequestBody TripSearchRequest request) {
        return ApiResponse.success(tripService.searchTrips(request), "Search Trip successfully");
    }

    @GetMapping("/{tripId}")
    public ApiResponse<TripDetailsResponse> getTripDetails(@PathVariable String tripId) {
        TripDetailsResponse tripDetails = tripService.getTripDetails(tripId);
        return ApiResponse.success(tripDetails, "Fetch Trip details successfully");
    }
}
