package com.group8.busbookingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusBookingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusBookingBackendApplication.class, args);
    }

}
