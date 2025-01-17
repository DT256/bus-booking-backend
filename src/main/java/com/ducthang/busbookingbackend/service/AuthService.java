package com.ducthang.busbookingbackend.service;

import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.entity.UserStatus;
import com.ducthang.busbookingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    // Đăng ký tài khoản mới
    public String register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists.";
        }
//        user.setActive(false);
        userRepository.save(user);
        return "User registered. Please check your email to activate your account.";
    }

    // Đăng nhập
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            if (user.getStatus() == UserStatus.ACTIVE) {
                return "Login successful.";
            } else {
                return "Account is not activated.";
            }
        }
        return "Invalid credentials.";
    }

    // Kích hoạt tài khoản qua OTP
    @Transactional
    public String activateAccount(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user != null && otpService.validateOtp(email, otp)) {
//            user.setActive(true);
            userRepository.save(user);
            otpService.clearOtp(email);  // Xóa OTP sau khi kích hoạt thành công
            return "Account activated.";
        }
        return "Invalid OTP.";
    }

    // Quên mật khẩu và gửi OTP
    public String forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String otp = otpService.generateOtp(email); // Tạo OTP
            emailService.sendOtp(email, otp); // Gửi OTP qua email
            return "OTP sent to your email.";
        }
        return "Email not found.";
    }
}


