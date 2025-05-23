package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.group8.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.group8.busbookingbackend.dto.auth.response.AuthResponse;
import com.group8.busbookingbackend.dto.auth.response.LoginResponse;
import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.service.IAuthService;
import com.group8.busbookingbackend.service.IEmailService;
import com.group8.busbookingbackend.service.IOTPService;
import com.group8.busbookingbackend.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOTPService otpService;

    @Autowired
    private IEmailService emailService;

    @GetMapping("/check-email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse userData = userService.findUserResponseByEmail(email);
        return ApiResponse.success(userData, "User fetched successfully");
    }

    // Đăng ký tài khoản mới
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody UserCreateRequest user) {
        return ApiResponse.success(authService.register(user),"Register successfully");
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid UserLoginRequest request) {
        LoginResponse authResponse = authService.login(request);

        ApiResponse<LoginResponse> response = new ApiResponse<>();
        response.setStatus("success");
        response.setCode(200);
        response.setMessage(authResponse.getMessage());
        response.setData(authResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam String email){
        boolean message = emailService.sendOtp(email,otpService.generateOtp(email));
        return ApiResponse.success(null, "Send otp successfully");
    }

    @PostMapping("validate-otp")
    public ApiResponse<String> validateOtp(@RequestParam String email, @RequestParam String otp){
        boolean isValid = otpService.validateOtp(email, otp);

        return isValid ?ApiResponse.success("OTP is valid!", "Validation successful") : ApiResponse.error(400,"Invalid OTP","The OTP you entered is incorrect or has expired.");
    }

    @PostMapping("/active-account")
    public ApiResponse<String> activeAccount(@RequestParam String email, @RequestParam String otp){
        boolean isActive = authService.activateAccount(email,otp);

        return isActive ?ApiResponse.success("OTP is valid!", "Account active successful") : ApiResponse.error(400,"Invalid OTP","The OTP you entered is incorrect or has expired.");
    }



    // Quên mật khẩu và gửi OTP
    @PostMapping("/forgot-password")
    public ApiResponse<String> resetPassword(@RequestParam String email, @RequestParam String newPassword)
    {
        boolean isSuccess = authService.resetPassword(email, newPassword);
        return isSuccess ?ApiResponse.success("Success","Reset password successfully") : ApiResponse.error(400,"Error","Error");
    }
}

