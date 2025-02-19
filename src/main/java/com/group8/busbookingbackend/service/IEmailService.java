package com.group8.busbookingbackend.service;

public interface IEmailService {
    public boolean sendOtp(String email, String otp);
}
