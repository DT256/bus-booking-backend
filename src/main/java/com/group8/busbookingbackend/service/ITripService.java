package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.trip.request.TripSearchRequest;
import com.group8.busbookingbackend.dto.trip.response.TripSearchResponse;

import java.util.List;

public interface ITripService {
    public List<TripSearchResponse> searchTrips(TripSearchRequest request);
}
