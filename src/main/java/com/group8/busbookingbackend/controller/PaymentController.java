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
    public ResponseEntity<ApiResponse<String>> bankPayCallbackHandler(HttpServletRequest request) {
        System.out.println("bankPayCallbackHandler");
        String status = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String bankCode = request.getParameter("vnp_BankCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        BigDecimal amount = new BigDecimal(request.getParameter("vnp_Amount"))
                .divide(BigDecimal.valueOf(100));

        String payDate = request.getParameter("vnp_PayDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(payDate, formatter);
        System.out.println(request.getParameter("orderid"));
        ObjectId bookingId = new ObjectId(request.getParameter("orderid"));

        System.out.println("bookingId: " + bookingId);
        System.out.println("bookingId: " + bankCode);

        if (status.equals("00")) {
            paymentService.handlePayBank(transactionNo, bankCode, transactionStatus, localDateTime, amount, bookingId);

            // Trả về response thành công
            ApiResponse<String> response = ApiResponse.success("Thanh toán thành công", "Đơn hàng đã thanh toán thành công.");
            return ResponseEntity.ok(response); // Trả về 200 OK với message thành công
        } else {
            // Trả về response lỗi
            ApiResponse<String> response = ApiResponse.error(400, "Thanh toán thất bại", "Vui lòng thử lại sau.");
            return ResponseEntity.badRequest().body(response); // Trả về 400 Bad Request với message lỗi
        }
    }
}

