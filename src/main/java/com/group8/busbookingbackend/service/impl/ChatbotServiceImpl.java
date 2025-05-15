package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.booking.response.BookingResponse;
import com.group8.busbookingbackend.dto.chatbot.ChatCompletionRequest;
import com.group8.busbookingbackend.dto.chatbot.ChatCompletionResponse;
import com.group8.busbookingbackend.dto.chatbot.ChatMessage;
import com.group8.busbookingbackend.entity.AddressEntity;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.RouteEntity;
import com.group8.busbookingbackend.repository.AddressRepository;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.repository.RouteRepository;
import com.group8.busbookingbackend.repository.UserRepository;
import com.group8.busbookingbackend.service.IBookingService;
import com.group8.busbookingbackend.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements IChatbotService {
    private final WebClient webClient;
    private final RouteRepository routeRepository;
    private final BookingRepository bookingRepository;
    private final AddressRepository addressRepository;
    private final IBookingService bookingService;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${openrouter.api.url}")
    private String openRouterApiUrl;

    @Value("${openrouter.api.model}")
    private String model;

    @Override
    public ChatMessage processMessage(ObjectId userId, String content) {
        // Phân tích nội dung tin nhắn để xác định intent
        if (content.toLowerCase().contains("tìm tuyến") || content.toLowerCase().contains("tìm chuyến")) {
            return handleRouteSearch(content);
        } else if (content.toLowerCase().contains("hủy vé") || content.toLowerCase().contains("hủy chuyến")) {
            return handleBookingCancellation(content, userId);
        }
        else if (content.toLowerCase().contains("xem vé")
                || content.toLowerCase().contains("vé của tôi")
                || content.toLowerCase().contains("xem lịch sử")
                || content.toLowerCase().contains("xem đặt vé")) {
            return handleViewBookings(userId);
        }

        // Nếu không phải intent đặc biệt, gửi đến OpenRouter API
        return getChatCompletion(content);
    }

    private ChatMessage handleRouteSearch(String content) {

        String startPoint = extractLocation(content, "từ", "đến");
        String endPoint = extractLocation(content, "đến", null);

        if (startPoint == null || endPoint == null) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Vui lòng cung cấp điểm đi và điểm đến. Ví dụ: 'Tìm tuyến từ Hà Nội đến Hồ Chí Minh'")
                    .build();
        }

        try {
            // Find all addresses for start and end cities
            List<AddressEntity> startAddresses = addressRepository.findByCity(startPoint);
            List<AddressEntity> endAddresses = addressRepository.findByCity(endPoint);

            if (startAddresses.isEmpty() || endAddresses.isEmpty()) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, chúng tôi hiện chưa hỗ trợ tuyến đường từ " +
                                capitalizeLocation(startPoint) + " đến " + capitalizeLocation(endPoint) + ".")
                        .build();
            }

            // Collect all possible start and end point ObjectIds
            List<ObjectId> startPointIds = startAddresses.stream()
                    .map(AddressEntity::getId)
                    .collect(Collectors.toList());
            List<ObjectId> endPointIds = endAddresses.stream()
                    .map(AddressEntity::getId)
                    .collect(Collectors.toList());

            // Find routes matching any combination of start and end points with status ACTIVE
            List<RouteEntity> routes = routeRepository.findByStartPointInAndEndPointInAndStatus(
                    startPointIds, endPointIds, RouteEntity.RouteStatus.ACTIVE);

            if (routes.isEmpty()) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, chúng tôi hiện chưa hỗ trợ tuyến đường từ " +
                                capitalizeLocation(startPoint) + " đến " + capitalizeLocation(endPoint) + ".")
                        .build();
            }

            StringBuilder response = new StringBuilder("Tôi đã tìm thấy các tuyến xe sau:\n\n");
            for (RouteEntity route : routes) {
                response.append("- Tuyến: ").append(route.getDescription())
                        .append("\n  Khoảng cách: ").append(route.getDistance()).append(" km")
                        .append("\n  Thời gian: ").append(route.getDuration()).append(" giờ\n\n");
            }

            return ChatMessage.builder()
                    .role("assistant")
                    .content(response.toString())
                    .build();
        } catch (Exception e) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Xin lỗi, có lỗi xảy ra khi tìm kiếm tuyến xe: " + e.getMessage())
                    .build();
        }
    }

    private ChatMessage handleViewBookings(ObjectId userId) {
        List<BookingEntity> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(booking -> booking.getStatus() != BookingEntity.BookingStatus.CANCELLED)
                .toList();;

        if (bookings.isEmpty()) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Bạn chưa có vé nào được đặt.")
                    .build();
        }

        StringBuilder response = new StringBuilder("Dưới đây là các vé bạn đã đặt:\n\n");

        for (BookingEntity booking : bookings) {
            response.append("- Mã vé: ").append(booking.getBookingCode())
                    .append("\n  Trạng thái: ").append(getStatusInVietnamese(booking.getStatus()))
                    .append("\n  Ngày đặt: ").append(booking.getCreatedAt())
                    .append("\n\n");
        }

        return ChatMessage.builder()
                .role("assistant")
                .content(response.toString())
                .build();
    }

    // Hàm chuyển đổi BookingStatus sang tiếng Việt
    private String getStatusInVietnamese(BookingEntity.BookingStatus status) {
        switch (status) {
            case PENDING:
                return "Đang chờ";
            case CONFIRMED:
                return "Đã xác nhận";
            case CANCELLED:
                return "Đã hủy";
            case COMPLETED:
                return "Đã hoàn thành";
            default:
                return status.toString(); // Fallback nếu có trạng thái không xác định
        }
    }

    private ChatMessage handleBookingCancellation(String content, ObjectId userId) {
        // Nếu chỉ có "hủy vé" mà không có mã
        String[] tokens = content.trim().split("\\s+");
        if (tokens.length <= 2) {
            // Tìm các vé đang chờ xác nhận (PENDING)
            List<BookingEntity> pendingBookings = bookingRepository.findByUserIdAndStatus(userId, BookingEntity.BookingStatus.PENDING);

            if (pendingBookings.isEmpty()) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Hiện bạn không có vé nào đang chờ xác nhận để hủy.")
                        .build();
            }

            StringBuilder response = new StringBuilder("Bạn đang có các vé **đang chờ xác nhận** sau:\n\n");
            for (BookingEntity booking : pendingBookings) {
                response.append("- Mã vé: ").append(booking.getBookingCode())
                        .append("\n  Ngày đặt: ").append(booking.getCreatedAt())
                        .append("\n\n");
            }

            response.append("Vui lòng nhập mã vé bạn muốn hủy. Ví dụ: *\"Hủy vé ABC123\"*");

            return ChatMessage.builder()
                    .role("assistant")
                    .content(response.toString())
                    .build();
        }

        // Trường hợp có mã vé
        String bookingCode = tokens[2].toUpperCase();

        try {
            BookingResponse response = bookingService.cancelBooking(bookingCode);

            return ChatMessage.builder()
                    .role("assistant")
                    .content("Vé **" + response.getBookingCode() + "** đã được hủy thành công." +
                            "\n• Trạng thái: " + response.getStatus() +
                            "\nCảm ơn bạn đã sử dụng dịch vụ!")
                    .build();
        } catch (IllegalArgumentException | IllegalStateException | SecurityException ex) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace(); // Log nếu cần
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Đã xảy ra lỗi khi hủy vé. Vui lòng thử lại sau.")
                    .build();
        }
    }




    private ChatMessage getChatCompletion(String content) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role("user")
                .content(content)
                .build());
        System.out.println("model = " + model);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(0.7)
                .max_tokens(2000)
                .build();

        ChatCompletionResponse response = webClient.post()
                .uri(openRouterApiUrl)
                .header("Authorization", "Bearer " + openRouterApiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block();

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage();
        }

        return ChatMessage.builder()
                .role("assistant")
                .content("Xin lỗi, tôi không thể xử lý yêu cầu của bạn lúc này.")
                .build();
    }


    private String capitalizeLocation(String location) {
        if (location == null || location.isBlank()) return location;

        String[] words = location.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }


    // Các phương thức hỗ trợ
    private String extractLocation(String content, String startMarker, String endMarker) {
        content = content.toLowerCase();
        String[] words = content.split("\\s+");
        
        if (startMarker != null && endMarker != null) {
            // Tìm vị trí của startMarker và endMarker
            int startIndex = content.indexOf(startMarker);
            int endIndex = content.indexOf(endMarker);
            
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                // Lấy text giữa startMarker và endMarker
                String location = content.substring(startIndex + startMarker.length(), endIndex).trim();
                // Loại bỏ các từ không cần thiết
                location = location.replaceAll("(từ|đến|tại|ở|tại|từ|đến|tại|ở|tại)", "").trim();
                return location.isEmpty() ? null : location;
            }
        } else if (startMarker != null) {
            // Trường hợp chỉ có startMarker
            int startIndex = content.indexOf(startMarker);
            if (startIndex != -1) {
                String location = content.substring(startIndex + startMarker.length()).trim();
                // Loại bỏ các từ không cần thiết
                location = location.replaceAll("(từ|đến|tại|ở|tại|từ|đến|tại|ở|tại)", "").trim();
                return location.isEmpty() ? null : location;
            }
        }
        
        return null;
    }


} 