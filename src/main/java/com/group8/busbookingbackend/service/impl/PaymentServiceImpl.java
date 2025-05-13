package com.group8.busbookingbackend.service.impl;


import com.group8.busbookingbackend.config.VNPAYConfig;
import com.group8.busbookingbackend.dto.payment.PaymentDTO;
import com.group8.busbookingbackend.entity.BookingEntity;
import com.group8.busbookingbackend.entity.PaymentEntity;
import com.group8.busbookingbackend.repository.BookingRepository;
import com.group8.busbookingbackend.repository.PaymentRepository;
import com.group8.busbookingbackend.service.IPaymentService;
import com.group8.busbookingbackend.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final VNPAYConfig vnPayConfig;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentDTO createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String orderId = request.getParameter("orderId");

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
    @Override
    public void handlePayBank(String transactionNo, String bankCode, String transactionStatus, LocalDateTime localDateTime, BigDecimal amount, ObjectId bookingId){
        Optional<BookingEntity> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found with ID: " + bookingId.toHexString());
        }

        BookingEntity order = bookingOpt.get();
        order.setPaymentStatus(BookingEntity.PaymentStatus.PAID);
        order.setPaymentMethod(BookingEntity.PaymentMethod.BANK_TRANSFER);

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setTransactionID(transactionNo);
        paymentEntity.setPaymentMethod(bankCode);
        paymentEntity.setPaymentDate(localDateTime);
        paymentEntity.setTotal(amount);
        paymentEntity.setBookingId(bookingId);

        paymentRepository.save(paymentEntity);
        //order.setPayment(paymentEntity);
        bookingRepository.save(order);



//        EmailDetail emailDetail = new EmailDetail();
//
//        String body = "Chào " + order.getUser().getName() + ",\n\n" +
//                "Chúng tôi xác nhận rằng bạn đã thanh toán thành công cho đơn hàng (Mã đơn hàng: " + orderId + ").\n" +
//                "Số tiền thanh toán: " + amount + " VND.\n" +
//                "Ngày thanh toán: " + localDateTime + ".\n\n" +
//                "Cảm ơn bạn đã tin tưởng và mua sắm tại cửa hàng của chúng tôi.\n\n" +
//                "Trân trọng,\nYour Company Name";
//        emailDetail.setMsgBody(body);
//        emailDetail.setRecipient(order.getUser().getEmail());
//        emailDetail.setSubject("Thông báo thánh toán đơn hàng");
//        emailService.sendInvoice(emailDetail);
    }

}
