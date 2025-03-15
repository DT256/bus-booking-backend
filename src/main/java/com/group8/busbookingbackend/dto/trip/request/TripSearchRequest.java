package com.group8.busbookingbackend.dto.trip.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripSearchRequest {
    private String startCity;          // Điểm đi (bắt buộc)
    private String endCity;            // Điểm đến (bắt buộc)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureDate; // Ngày khởi hành (bắt buộc)
    private Double minPrice;           // Giá tối thiểu (tùy chọn)
    private Double maxPrice;           // Giá tối đa (tùy chọn)
    private String busType;            // Loại xe (tùy chọn, ví dụ: "SEATER", "SLEEPER")
    private Integer minAvailableSeats; // Số ghế trống tối thiểu (tùy chọn)
    private String sortBy;             // Sắp xếp theo: "departureTime" hoặc "price" (mặc định: "departureTime")
    private String sortOrder;          // Thứ tự: "asc" hoặc "desc" (mặc định: "asc")
}
