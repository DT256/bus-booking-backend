package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.ReviewEntity;
import com.group8.busbookingbackend.entity.TripEntity;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.repository.ReviewRepository;
import com.group8.busbookingbackend.repository.TripRepository;
import com.group8.busbookingbackend.service.ICloudinaryService;
import com.group8.busbookingbackend.service.IReviewService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl  implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ICloudinaryService cloudinaryService;

    @Override
    public ReviewResponse createReview(ObjectId userId, ReviewRequest requestDto) {
        ObjectId bookingId = new ObjectId(requestDto.getBookingId());

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + requestDto.getBookingId()));

//        if (!booking.getUserId().toString().equals(userId)) {
//            throw new RuntimeException("You are not authorized to review this booking.");
//        }

        TripEntity trip = tripRepository.findById(booking.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + booking.getTripId()));
        if (trip.getStatus() != TripEntity.TripStatus.COMPLETED) {
            throw new RuntimeException("Cannot review trip. Trip is not completed yet.");
        }

        if (reviewRepository.findByBookingId(bookingId).isPresent()) {
            throw new RuntimeException("This booking has already been reviewed.");
        }

        //Upload ảnh lên Cloudinary
        List<String> imageUrls = requestDto.getImages() != null
                ? requestDto.getImages().stream().map(image -> {
            try {
                return cloudinaryService.uploadImage(image);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image: " + image.getOriginalFilename(), e);
            }
        }).collect(Collectors.toList())
                : Collections.emptyList();

        ReviewEntity review = ReviewEntity.builder()
                .bookingId(bookingId)
                .userId(userId)
                .rating(requestDto.getRating())
                .comment(requestDto.getComment())
                .imageUrls(imageUrls)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        review = reviewRepository.save(review);

        ReviewResponse responseDto = new ReviewResponse();
        responseDto.setId(review.getId().toString());
        responseDto.setBookingId(review.getBookingId().toString());
        responseDto.setUserId(review.getUserId().toString());
        responseDto.setRating(review.getRating());
        responseDto.setComment(review.getComment());
        responseDto.setImageUrls(review.getImageUrls());
        responseDto.setCreatedAt(review.getCreatedAt());
        responseDto.setUpdatedAt(review.getUpdatedAt());

        return responseDto;
    }


    // Lấy danh sách đánh giá theo routeId
    public List<ReviewResponse> getReviewsByRoute(String routeId) {
        ObjectId id = new ObjectId(routeId);

        // Bước 1: Tìm tất cả trips theo routeId
        List<TripEntity> trips = tripRepository.findByRouteId(id);
        if (trips.isEmpty()) {
            return Collections.emptyList(); // Không có chuyến nào thuộc tuyến này
        }

        // Bước 2: Lấy danh sách tripId
        List<ObjectId> tripIds = trips.stream()
                .map(TripEntity::getId)
                .collect(Collectors.toList());

        // Bước 3: Tìm tất cả bookings theo tripIds
        List<BookingEntity> bookings = bookingRepository.findByTripIds(tripIds);
        if (bookings.isEmpty()) {
            return Collections.emptyList(); // Không có booking nào
        }

        // Bước 4: Lấy danh sách bookingId
        List<ObjectId> bookingIds = bookings.stream()
                .map(BookingEntity::getId)
                .collect(Collectors.toList());

        // Bước 5: Tìm tất cả reviews theo bookingIds
        List<ReviewEntity> reviews = reviewRepository.findByBookingIds(bookingIds);
        if (reviews.isEmpty()) {
            return Collections.emptyList(); // Không có đánh giá nào
        }

        // Bước 6: Chuyển đổi sang DTO
        return reviews.stream().map(review -> {
            ReviewResponse dto = new ReviewResponse();
            dto.setId(review.getId().toString());
            dto.setBookingId(review.getBookingId().toString());
            dto.setUserId(review.getUserId().toString());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setImageUrls(review.getImageUrls());
            dto.setCreatedAt(review.getCreatedAt());
            dto.setUpdatedAt(review.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }



}
