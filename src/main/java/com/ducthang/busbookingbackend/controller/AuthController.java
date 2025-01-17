package com.ducthang.busbookingbackend.controller;

import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String result = authService.login(email, password);
        if (result.startsWith("Invalid credentials") || result.startsWith("Account is not activated")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
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
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        String result = authService.forgotPassword(email);
        if (result.startsWith("Email not found")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}

