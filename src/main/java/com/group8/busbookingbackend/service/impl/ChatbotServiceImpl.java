package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.chatbot.ChatCompletionRequest;
import com.group8.busbookingbackend.dto.chatbot.ChatCompletionResponse;
import com.group8.busbookingbackend.dto.chatbot.ChatMessage;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.RouteEntity;
import com.group8.busbookingbackend.repository.AddressRepository;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.repository.RouteRepository;
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

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements IChatbotService {
    private final WebClient webClient;
    private final RouteRepository routeRepository;
    private final BookingRepository bookingRepository;
    private final AddressRepository addressRepository;

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
        } else if (content.toLowerCase().contains("đặt vé") || content.toLowerCase().contains("đặt chỗ")) {
            return handleBookingRequest(content, userId);
        } else if (content.toLowerCase().contains("hủy vé") || content.toLowerCase().contains("hủy chuyến")) {
            return handleBookingCancellation(content, userId);
        }

        // Nếu không phải intent đặc biệt, gửi đến OpenRouter API
        return getChatCompletion(content);
    }

    private ChatMessage handleRouteSearch(String content) {
        System.out.println(content);
        String startPoint = extractLocation(content, "từ", "đến");
        String endPoint = extractLocation(content, "đến", null);
        System.out.println(startPoint);
        System.out.println(endPoint);

        if (startPoint == null || endPoint == null) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Vui lòng cung cấp điểm đi và điểm đến. Ví dụ: 'Tìm tuyến từ Hà Nội đến Hồ Chí Minh'")
                    .build();
        }

        try {
            // Kiểm tra địa chỉ có tồn tại trong DB không
            var startAddress = addressRepository.findByCity(startPoint);
            var endAddress = addressRepository.findByCity(endPoint);

            if (startAddress == null || endAddress == null) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, chúng tôi hiện chưa hỗ trợ tuyến đường từ " + capitalizeLocation(startPoint) + " đến " + capitalizeLocation(endPoint) + ".")
                        .build();
            }

            // Tìm các tuyến xe phù hợp
            List<RouteEntity> routes = routeRepository.findByStartPointAndEndPoint(
                    startAddress.getId(),
                    endAddress.getId()
            );

            if (routes.isEmpty()) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, chúng tôi hiện chưa hỗ trợ tuyến đường từ " + capitalizeLocation(startPoint) + " đến " + capitalizeLocation(endPoint) + ".")
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


    private ChatMessage handleBookingRequest(String content, ObjectId userId) {
        // Trích xuất thông tin đặt vé
        String routeId = extractRouteId(content);
        String pickupTime = extractDateTime(content);
        int numberOfPassengers = extractNumberOfPassengers(content);

        if (routeId == null) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Vui lòng cung cấp mã tuyến xe bạn muốn đặt. Ví dụ: 'Đặt vé tuyến R001'")
                    .build();
        }

        try {
            // Kiểm tra tuyến xe có tồn tại và còn hoạt động không
            RouteEntity route = routeRepository.findById(new ObjectId(routeId))
                    .orElse(null);

            if (route == null || route.getStatus() != RouteEntity.RouteStatus.ACTIVE) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, tuyến xe không tồn tại hoặc đã ngừng hoạt động.")
                        .build();
            }

            // Tạo booking mới
            BookingEntity booking = BookingEntity.builder()
                    .tripId(new ObjectId(routeId))
                    .userId(userId)
                    .status(BookingEntity.BookingStatus.PENDING)
                    .paymentStatus(BookingEntity.PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            bookingRepository.save(booking);

            return ChatMessage.builder()
                    .role("assistant")
                    .content("Đặt vé thành công! Mã đặt vé của bạn là: " + booking.getBookingCode() +
                            "\nVui lòng thanh toán trong vòng 30 phút để hoàn tất đặt vé.")
                    .build();
        } catch (IllegalArgumentException e) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Xin lỗi, mã tuyến xe không hợp lệ.")
                    .build();
        }
    }

    private ChatMessage handleBookingCancellation(String content, ObjectId userId) {
        // Trích xuất mã đặt vé
        String bookingCode = extractBookingCode(content);

        if (bookingCode == null) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Vui lòng cung cấp mã đặt vé bạn muốn hủy. Ví dụ: 'Hủy vé B001'")
                    .build();
        }

        try {
            // Tìm booking theo mã
            BookingEntity booking = bookingRepository.findByBookingCode(bookingCode)
                    .orElse(null);

            if (booking == null) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, không tìm thấy đặt vé với mã: " + bookingCode)
                        .build();
            }

            // Kiểm tra xem người dùng có quyền hủy vé không
            if (!booking.getUserId().toString().equals(userId)) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Xin lỗi, bạn không có quyền hủy đặt vé này.")
                        .build();
            }

            // Kiểm tra trạng thái đặt vé
            if (booking.getStatus() == BookingEntity.BookingStatus.CANCELLED) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Đặt vé này đã được hủy trước đó.")
                        .build();
            }

            if (booking.getStatus() == BookingEntity.BookingStatus.COMPLETED) {
                return ChatMessage.builder()
                        .role("assistant")
                        .content("Không thể hủy đặt vé đã hoàn thành.")
                        .build();
            }

            // Hủy đặt vé
            booking.setStatus(BookingEntity.BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            return ChatMessage.builder()
                    .role("assistant")
                    .content("Hủy đặt vé thành công! Mã đặt vé " + bookingCode + " đã được hủy.")
                    .build();
        } catch (Exception e) {
            return ChatMessage.builder()
                    .role("assistant")
                    .content("Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu hủy vé.")
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

    private String extractRouteId(String content) {
        Pattern pattern = Pattern.compile("tuyến\\s*([A-Z0-9]+)");
        Matcher matcher = pattern.matcher(content.toLowerCase());
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractDateTime(String content) {
        Pattern pattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{2})");
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    private int extractNumberOfPassengers(String content) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*người");
        Matcher matcher = pattern.matcher(content.toLowerCase());
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 1;
    }

    private String extractBookingCode(String content) {
        Pattern pattern = Pattern.compile("([A-Z0-9]+)");
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }


} 