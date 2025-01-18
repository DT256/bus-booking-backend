package com.ducthang.busbookingbackend.controller;

import com.ducthang.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.ducthang.busbookingbackend.dto.auth.request.UserResetPasswordRequest;
import com.ducthang.busbookingbackend.dto.auth.response.AuthResponse;
import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/check-email/{email}")
    public ResponseEntity<AuthResponse> checkEmail(@PathVariable String email) {
        authService.checkEmail();
    }


    // Đăng ký tài khoản mới
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String result = authService.register(user);
        if (result.startsWith("Email already exists")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Đăng nhập
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid UserLoginRequest request) {
        return authService.login(request);
    }

    // Kích hoạt tài khoản qua OTP
    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String email, @RequestParam String otp) {
        String result = authService.activateAccount(email, otp);
        if (result.startsWith("Invalid OTP")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Quên mật khẩu và gửi OTP
    @PostMapping("/forgot-password")
    public AuthResponse resetPassword(@RequestBody @Valid UserResetPasswordRequest request)
    {
        return authService.forgotPassword(request);
    }
}

