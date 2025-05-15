package com.group8.busbookingbackend.controller;


import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.payment.PaymentDTO;
import com.group8.busbookingbackend.service.IPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private final IPaymentService paymentService;

    @GetMapping("/vn-pay")
    public ResponseEntity<ApiResponse<PaymentDTO>> bankPay(HttpServletRequest request) {
        PaymentDTO dto = paymentService.createVnPayPayment(request);
        ApiResponse<PaymentDTO> response = ApiResponse.success(dto, "Tạo URL thanh toán VNPay thành công");
        return ResponseEntity.ok(response); // Trả về 200 OK với ApiResponse
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<?> bankPayCallbackHandler(HttpServletRequest request) {
        System.out.println("bankPayCallbackHandler");

        // Retrieve query parameters
        String status = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String bankCode = request.getParameter("vnp_BankCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        String amountStr = request.getParameter("vnp_Amount");
        String payDate = request.getParameter("vnp_PayDate");
        String orderId = request.getParameter("orderid");

        // Validate required parameters
        if (orderId == null || status == null) {
            return ResponseEntity.badRequest().body("Missing required parameters.");
        }

        // Parse amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr).divide(BigDecimal.valueOf(100));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid amount format.");
        }

        // Parse pay date
        LocalDateTime localDateTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            localDateTime = LocalDateTime.parse(payDate, formatter);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid pay date format.");
        }

        // Parse booking ID
        ObjectId bookingId;
        try {
            bookingId = new ObjectId(orderId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid booking ID format.");
        }

        System.out.println("bookingId: " + bookingId);
        System.out.println("bankCode: " + bankCode);

        // Process payment and redirect
        String redirectUrl;
        if ("00".equals(status)) {
            // Handle successful payment
            paymentService.handlePayBank(transactionNo, bankCode, transactionStatus, localDateTime, amount, bookingId);
            redirectUrl = "yourapp://ticket?bookingId=" + orderId + "&status=success";
        } else {
            // Handle failed payment
            redirectUrl = "yourapp://ticket?bookingId=" + orderId + "&status=failure";
        }

        // Redirect to deep link
        try {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid redirect URL.");
        }
    }
}

