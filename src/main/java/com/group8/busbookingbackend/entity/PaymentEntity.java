package com.group8.busbookingbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class PaymentEntity {
    @Id
    private ObjectId paymentID;

    @Field("transaction_id")
    private String transactionID;

    @Field("payment_method")
    private String paymentMethod;

    @Field("payment_date")
    private LocalDateTime paymentDate;

    @Field("total")
    private BigDecimal total;


    @Field("order_id")
    private ObjectId bookingId;

}



