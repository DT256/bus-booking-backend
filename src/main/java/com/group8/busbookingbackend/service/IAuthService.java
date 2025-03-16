package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.group8.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.group8.busbookingbackend.dto.auth.response.AuthResponse;

public interface IAuthService {
    public AuthResponse register(UserCreateRequest request);
    public AuthResponse login(UserLoginRequest request);
    public boolean activateAccount(String email, String otp);
    public boolean resetPassword(String email, String password);
}
