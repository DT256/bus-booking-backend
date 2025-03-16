package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.trip.request.TripSearchRequest;
import com.group8.busbookingbackend.dto.trip.response.TripDetailsResponse;
import com.group8.busbookingbackend.dto.trip.response.TripSearchResponse;
import com.group8.busbookingbackend.entity.*;
import com.group8.busbookingbackend.repository.*;
import com.group8.busbookingbackend.service.ITripService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements ITripService {
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private TripSeatRepository tripSeatRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<TripSearchResponse> searchTrips(TripSearchRequest request) {
        // Tìm các tuyến đường phù hợp
        List<RouteEntity> routes = routeRepository.findByStartPointAndEndPoint(
                addressRepository.findByCity(request.getStartCity()).getId(),
                addressRepository.findByCity(request.getEndCity()).getId());
        System.out.println(routes.size());
        if (routes.isEmpty()) {
            return new ArrayList<>();
        }

        // Lấy khoảng thời gian trong ngày
        LocalDateTime startOfDay = request.getDepartureDate().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Tìm các chuyến đi
        List<TripSearchResponse> result = new ArrayList<>();
        for (RouteEntity route : routes) {
            List<TripEntity> trips = tripRepository.findByRouteIdAndDepartureTimeRange(
                    route.getId(), startOfDay, endOfDay);

            for (TripEntity trip : trips) {
                // Lấy thông tin xe
                BusEntity bus = busRepository.findById(trip.getBusId())
                        .orElseThrow(() -> new RuntimeException("Bus not found"));

                // Đếm số ghế trống
                int availableSeats = tripSeatRepository.findAvailableSeatsByTrip(trip.getId()).size();

                // Lấy loại xe (dựa trên ghế đầu tiên, giả định xe chỉ có 1 loại ghế)
                List<SeatEntity> allSeats = seatRepository.findByBusId(trip.getBusId());
                String busType = allSeats.isEmpty() ? "UNKNOWN" : allSeats.get(0).getType().toString();

                // Lọc theo các tiêu chí
                if (request.getMinPrice() != null && trip.getPrice() < request.getMinPrice()) continue;
                if (request.getMaxPrice() != null && trip.getPrice() > request.getMaxPrice()) continue;
                if (request.getMinAvailableSeats() != null && availableSeats < request.getMinAvailableSeats()) continue;
                if (request.getBusType() != null && !request.getBusType().equalsIgnoreCase(busType)) continue;

                // Tạo DTO
                TripSearchResponse dto = new TripSearchResponse();
                dto.setId(trip.getId().toString());
                dto.setBus(busRepository.findById(trip.getBusId()).orElseThrow(() -> new RuntimeException("Bus not found")));
                dto.setRoute(routeRepository.findById(route.getId()).orElseThrow(() -> new RuntimeException("Route not found")));
                dto.setDepartureTime(trip.getDepartureTime());
                dto.setArrivalTime(trip.getArrivalTime());
                dto.setPrice(trip.getPrice());
//                dto.setStartPointCity(route.getStartPoint().getCity());
                dto.setStartPointCity(addressRepository.findById(route.getStartPoint()).get().getCity());
                dto.setEndPointCity(addressRepository.findById(route.getEndPoint()).get().getCity());
                dto.setAvailableSeats(availableSeats);
                dto.setBusType(busType);
                dto.setStatus(trip.getStatus().toString());

                result.add(dto);
            }
        }

        // Sắp xếp kết quả
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "departureTime";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "asc";

        Comparator<TripSearchResponse> comparator;
        if ("price".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingDouble(TripSearchResponse::getPrice);
        } else {
            comparator = Comparator.comparing(TripSearchResponse::getDepartureTime);
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return result.stream().sorted(comparator).collect(Collectors.toList());
    }


    public TripDetailsResponse getTripDetails(String tripId) {
        ObjectId id = new ObjectId(tripId);

        // Lấy thông tin chuyến đi
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));

        // Lấy thông tin xe
        BusEntity bus = busRepository.findById(trip.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found with ID: " + trip.getBusId()));

        // Lấy thông tin tuyến đường
        RouteEntity route = routeRepository.findById(trip.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found with ID: " + trip.getRouteId()));

        // Lấy thông tin địa chỉ bắt đầu
        AddressEntity startAddress = addressRepository.findById(route.getStartPoint())
                .orElseThrow(() -> new RuntimeException("Start address not found with ID: " + route.getStartPoint()));

        // Lấy thông tin địa chỉ kết thúc
        AddressEntity endAddress = addressRepository.findById(route.getEndPoint())
                .orElseThrow(() -> new RuntimeException("End address not found with ID: " + route.getEndPoint()));

        // Lấy danh sách trạng thái ghế
        List<TripSeatEntity> tripSeats = tripSeatRepository.findByTripId(id);

        // Tạo DTO và ánh xạ dữ liệu
        TripDetailsResponse dto = new TripDetailsResponse();
        dto.setId(trip.getId().toString());
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setArrivalTime(trip.getArrivalTime());
        dto.setPrice(trip.getPrice());
        dto.setStatus(trip.getStatus().name());

        // Ánh xạ thông tin xe
        TripDetailsResponse.BusDetails busDetails = new TripDetailsResponse.BusDetails();
        busDetails.setId(bus.getId().toString());
        busDetails.setLicensePlate(bus.getLicensePlate());
        busDetails.setCapacity(bus.getCapacity());
        busDetails.setCategoryId(bus.getCategoryId().toString());
        busDetails.setStatus(bus.getStatus().name());
        dto.setBus(busDetails);

        // Ánh xạ thông tin tuyến đường
        TripDetailsResponse.RouteDetails routeDetails = new TripDetailsResponse.RouteDetails();
        routeDetails.setId(route.getId().toString());
        routeDetails.setStartAddress(startAddress);
        routeDetails.setEndAddress(endAddress);
        routeDetails.setDistance(route.getDistance());
        routeDetails.setDuration(route.getDuration());
        routeDetails.setDescription(route.getDescription());
        routeDetails.setStatus(route.getStatus().name());
        dto.setRoute(routeDetails);

        // Ánh xạ danh sách ghế
        List<TripDetailsResponse.SeatDetails> seatDetailsList = tripSeats.stream().map(seat -> {
            SeatEntity seatEntity = seatRepository.findById(seat.getSeatId()).orElseThrow();

            TripDetailsResponse.SeatDetails seatDetails = new TripDetailsResponse.SeatDetails();
            seatDetails.setId(seat.getId().toString());
            seatDetails.setSeatId(seat.getSeatId().toString());
            seatDetails.setSeatNumber(seatEntity.getSeatNumber());
            seatDetails.setFloor(seatEntity.getFloor());
            seatDetails.setStatus(seat.getStatus().name());
            return seatDetails;
        }).collect(Collectors.toList());
        dto.setSeats(seatDetailsList);

        return dto;
    }


}