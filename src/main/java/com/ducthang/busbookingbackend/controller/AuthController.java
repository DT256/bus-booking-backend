package com.ducthang.busbookingbackend.controller;

import com.cloudinary.Api;
import com.ducthang.busbookingbackend.dto.ApiResponse;
import com.ducthang.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.ducthang.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.ducthang.busbookingbackend.dto.auth.request.UserResetPasswordRequest;
import com.ducthang.busbookingbackend.dto.auth.response.AuthResponse;
import com.ducthang.busbookingbackend.dto.user.response.UserResponse;
import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.service.AuthService;
import com.ducthang.busbookingbackend.service.IUserService;
import com.ducthang.busbookingbackend.service.OTPService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private IUserService userService;
    @Autowired
    private OTPService otpService;

    @GetMapping("/check-email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse userData = userService.findUserResponseByEmail(email);
        return ApiResponse.success(userData, "User fetched successfully");
    }

    // Đăng ký tài khoản mới
    @PostMapping("/register")
    public AuthResponse register(@RequestBody UserCreateRequest user) {
        return authService.register(user);
    }

    // Đăng nhập
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid UserLoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam String email){
        String message = otpService.sendOtp(email);
        return ApiResponse.success(message, "Send otp successfully");
    }

    @PostMapping("validate-otp")
    public ApiResponse<String> validateOtp(@RequestParam String email, @RequestParam String otp){
        boolean isValid = otpService.validateOtp(email, otp);

        return isValid ?ApiResponse.success(message, "Send otp successfully") : ApiResponse.error();
    }



    // Quên mật khẩu và gửi OTP
    @PostMapping("/forgot-password")
    public AuthResponse resetPassword(@RequestBody @Valid UserResetPasswordRequest request)
    {
        return authService.forgotPassword(request);
    }
}

