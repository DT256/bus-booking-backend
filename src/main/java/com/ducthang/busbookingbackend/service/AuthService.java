package com.ducthang.busbookingbackend.service;

import com.ducthang.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.ducthang.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.ducthang.busbookingbackend.dto.auth.request.UserResetPasswordRequest;
import com.ducthang.busbookingbackend.dto.auth.response.AuthResponse;
import com.ducthang.busbookingbackend.entity.Role;
import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.entity.UserStatus;
import com.ducthang.busbookingbackend.exception.AppException;
import com.ducthang.busbookingbackend.exception.ErrorCode;
import com.ducthang.busbookingbackend.repository.UserRepository;
import com.ducthang.busbookingbackend.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Đăng ký tài khoản mới
    public AuthResponse register(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXIST_REGISTER);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXIST_REGISTER);
        }

        User user = User.builder()
                        .name(request.getName())
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .build();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        user = userRepository.save(user);
        String token = JwtProvider.generateJwtToken(user);
        return new AuthResponse(token, "Register successfully");
    }

    // Đăng nhập
    public AuthResponse login(UserLoginRequest request) {
        User user = userService.findUserByEmail(request.getEmail());
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = JwtProvider.generateJwtToken(user);
        return new AuthResponse(token, "Login successfully");
    }

    // Kích hoạt tài khoản qua OTP
    @Transactional
    public String activateAccount(String email, String otp) {
        User user = userService.findUserByEmail(email);
        if (user != null && otpService.validateOtp(email, otp)) {
//            user.setActive(true);
            userRepository.save(user);
            otpService.clearOtp(email);  // Xóa OTP sau khi kích hoạt thành công
            return "Account activated.";
        }
        return "Invalid OTP.";
    }

    // Quên mật khẩu và gửi OTP
    public String forgotPassword(UserResetPasswordRequest request) throws MessagingException {
        User user = userService.findUserByEmail(request.getEmail());
        if (user != null) {
            String otp = otpService.generateOtp(request.getEmail()); // Tạo OTP
            emailService.sendOtp(request.getEmail(), otp); // Gửi OTP qua email
            return "OTP sent to your email.";
        }
        return "Email not found.";
    }
}
