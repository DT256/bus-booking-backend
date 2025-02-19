package com.group8.busbookingbackend.service;

public interface IOTPService {
    public String generateOtp(String email);
    public String sendOtp(String email);
    public boolean validateOtp(String email, String otp);
    public void clearOtp(String email);
}
