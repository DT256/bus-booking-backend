package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.stoppoint.StopPointDTO;
import com.group8.busbookingbackend.service.StopPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stop-points")
@RequiredArgsConstructor
public class StopPointController {
    private final StopPointService stopPointService;

    @GetMapping("/route/{routeId}")
    public ApiResponse<List<StopPointDTO>> getStopPointsByRouteId(@PathVariable String routeId) {
        return ApiResponse.success(stopPointService.getStopPointsByRouteId(routeId), "Lấy danh sách điểm dừng thành công");
    }

    @GetMapping("/route/{routeId}/pickup")
    public ApiResponse<List<StopPointDTO>> getPickupPointsByRouteId(@PathVariable String routeId) {
        return ApiResponse.success(stopPointService.getPickupPointsByRouteId(routeId), "Lấy danh sách điểm đón khách thành công");
    }

    @GetMapping("/route/{routeId}/dropoff")
    public ApiResponse<List<StopPointDTO>> getDropoffPointsByRouteId(@PathVariable String routeId) {
        return ApiResponse.success(stopPointService.getDropoffPointsByRouteId(routeId), "Lấy danh sách điểm trả khách thành công");
    }

    @GetMapping("/trip/{tripId}")
    public ApiResponse<List<StopPointDTO>> getStopPointsByTripId(@PathVariable String tripId) {
        return ApiResponse.success(stopPointService.getStopPointsByTripId(tripId), "Lấy danh sách điểm dừng của chuyến đi thành công");
    }

    @GetMapping("/trip/{tripId}/pickup")
    public ApiResponse<List<StopPointDTO>> getPickupPointsByTripId(@PathVariable String tripId) {
        return ApiResponse.success(stopPointService.getPickupPointsByTripId(tripId), "Lấy danh sách điểm đón khách của chuyến đi thành công");
    }

    @GetMapping("/trip/{tripId}/dropoff")
    public ApiResponse<List<StopPointDTO>> getDropoffPointsByTripId(@PathVariable String tripId) {
        return ApiResponse.success(stopPointService.getDropoffPointsByTripId(tripId), "Lấy danh sách điểm trả khách của chuyến đi thành công");
    }

    @PostMapping
    public ApiResponse<StopPointDTO> createStopPoint(@RequestBody StopPointDTO stopPointDTO) {
        return ApiResponse.success(stopPointService.createStopPoint(stopPointDTO), "Tạo điểm dừng thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<StopPointDTO> updateStopPoint(@PathVariable String id, @RequestBody StopPointDTO stopPointDTO) {
        return ApiResponse.success(stopPointService.updateStopPoint(id, stopPointDTO), "Cập nhật điểm dừng thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStopPoint(@PathVariable String id) {
        stopPointService.deleteStopPoint(id);
        return ApiResponse.success(null, "Xóa điểm dừng thành công");
    }
} 