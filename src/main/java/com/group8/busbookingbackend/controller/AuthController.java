package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.group8.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.group8.busbookingbackend.dto.auth.request.UserResetPasswordRequest;
import com.group8.busbookingbackend.dto.auth.response.AuthResponse;
import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.service.AuthService;
import com.group8.busbookingbackend.service.IUserService;
import com.group8.busbookingbackend.service.OTPService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

        return isValid ?ApiResponse.success("OTP is valid!", "Validation successful") : ApiResponse.error(400,"Invalid OTP","The OTP you entered is incorrect or has expired.");
    }



    // Quên mật khẩu và gửi OTP
    @PostMapping("/forgot-password")
    public ApiResponse<String> resetPassword(@RequestParam String email, @RequestParam String newPassword)
    {
        boolean isSuccess = authService.resetPassword(email, newPassword);
        return isSuccess ?ApiResponse.success("Success","Reset password successfully") : ApiResponse.error(400,"Error","Error");
    }
}

