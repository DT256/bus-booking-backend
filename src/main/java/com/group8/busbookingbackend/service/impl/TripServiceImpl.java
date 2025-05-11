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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private StopPointRepository stopPointRepository;

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

        // Thiết lập khoảng thời gian trong ngày để lọc chuyến theo ngày
        LocalDateTime startOfDay = request.getDepartureDate().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<TripSearchResponse> result = new ArrayList<>();

        for (RouteEntity route : routes) {
            // Tìm chuyến đi theo route và thời gian
            List<TripEntity> trips = tripRepository.findByRouteIdAndDepartureTimeRange(
                    route.getId(), startOfDay, endOfDay);

            for (TripEntity trip : trips) {
                // Lấy thông tin xe
                BusEntity bus = busRepository.findById(trip.getBusId())
                        .orElseThrow(() -> new RuntimeException("Bus not found"));

                // Đếm số ghế trống
                int availableSeats = tripSeatRepository.findAvailableSeatsByTrip(trip.getId()).size();

                // Xác định loại ghế (giả sử tất cả ghế cùng loại)
                List<SeatEntity> allSeats = seatRepository.findByBusId(trip.getBusId());
                String busType = allSeats.isEmpty() ? "UNKNOWN" : allSeats.get(0).getType().toString();

                // Lọc theo điều kiện tìm kiếm
                if (request.getMinPrice() != null && trip.getPrice() < request.getMinPrice()) continue;
                if (request.getMaxPrice() != null && trip.getPrice() > request.getMaxPrice()) continue;
                if (request.getMinAvailableSeats() != null && availableSeats < request.getMinAvailableSeats()) continue;
                if (request.getBusType() != null && !request.getBusType().equalsIgnoreCase(busType)) continue;

                // Lấy thành phố bắt đầu/kết thúc từ địa chỉ
                String startCity = addressRepository.findById(route.getStartPoint())
                        .map(AddressEntity::getCity).orElse("UNKNOWN");
                String endCity = addressRepository.findById(route.getEndPoint())
                        .map(AddressEntity::getCity).orElse("UNKNOWN");

                // Tạo DTO kết quả
                TripSearchResponse dto = TripSearchResponse.builder()
                        .id(trip.getId().toString())
                        .busId(bus.getId().toString())
                        .capacity(bus.getCapacity())
                        .avatar(bus.getImageUrls().get(0))
                        .departureTime(trip.getDepartureTime())
                        .arrivalTime(trip.getArrivalTime())
                        .duration(route.getDuration())
                        .distance(route.getDistance())
                        .price(trip.getPrice())
                        .startPointCity(startCity)
                        .endPointCity(endCity)
                        .availableSeats(availableSeats)
                        .busType(busType)
                        .status(trip.getStatus().toString())
                        .build();

                result.add(dto);
            }
        }

        // Sắp xếp danh sách kết quả
        String sortBy = Optional.ofNullable(request.getSortBy()).orElse("departureTime");
        String sortOrder = Optional.ofNullable(request.getSortOrder()).orElse("asc");

        Comparator<TripSearchResponse> comparator = "price".equalsIgnoreCase(sortBy)
                ? Comparator.comparingDouble(TripSearchResponse::getPrice)
                : Comparator.comparing(TripSearchResponse::getDepartureTime);

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return result.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
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

        // Lấy danh sách điểm dừng
        List<StopPointEntity> stopPoints = trip.getStopPointIds() != null ?
                stopPointRepository.findAllById(trip.getStopPointIds()) :
                Collections.emptyList();

        // Tạo DTO và ánh xạ dữ liệu
        TripDetailsResponse dto = new TripDetailsResponse();
        dto.setId(trip.getId().toString());
        dto.setBusId(bus.getId().toString());
        dto.setLicensePlate(bus.getLicensePlate());
        dto.setCapacity(bus.getCapacity());
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setPrice(trip.getPrice());
        dto.setStatus(trip.getStatus().name());
        dto.setStartPointCity(startAddress.getCity());
        dto.setEndPointCity(endAddress.getCity());

        // Ánh xạ danh sách ghế
        List<TripDetailsResponse.SeatDetails> seatDetailsList = tripSeats.stream().map(seat -> {
            SeatEntity seatEntity = seatRepository.findById(seat.getSeatId()).orElseThrow();

            TripDetailsResponse.SeatDetails seatDetails = new TripDetailsResponse.SeatDetails();
            seatDetails.setSeatId(seat.getSeatId().toString());
            seatDetails.setSeatNumber(seatEntity.getSeatNumber());
            seatDetails.setFloor(seatEntity.getFloor());
            seatDetails.setStatus(seat.getStatus().name());
            return seatDetails;
        }).collect(Collectors.toList());
        dto.setSeats(seatDetailsList);

        // Ánh xạ danh sách điểm dừng
        List<TripDetailsResponse.StopPointDetails> stopPointDetailsList = stopPoints.stream().map(stopPoint -> {
            TripDetailsResponse.StopPointDetails stopPointDetails = new TripDetailsResponse.StopPointDetails();
            stopPointDetails.setId(stopPoint.getId().toString());
            stopPointDetails.setName(stopPoint.getName());
            stopPointDetails.setAddress(stopPoint.getAddress());
            stopPointDetails.setOrderNumber(stopPoint.getOrderNumber());
            stopPointDetails.setType(stopPoint.getType().name());
            stopPointDetails.setEstimatedTime(stopPoint.getEstimatedTime());
            return stopPointDetails;
        }).collect(Collectors.toList());
        dto.setStopPoints(stopPointDetailsList);

        return dto;
    }
}