package com.group8.busbookingbackend.service;


import com.group8.busbookingbackend.dto.payment.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IPaymentService {
    public PaymentDTO createVnPayPayment(HttpServletRequest request);
    void handlePayBank(String transactionNo, String bankCode, String transactionStatus, LocalDateTime localDateTime, BigDecimal amount, ObjectId bookingId);

}
