package com.group8.busbookingbackend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    public String code;
    public String message;
    public String paymentUrl;
}
