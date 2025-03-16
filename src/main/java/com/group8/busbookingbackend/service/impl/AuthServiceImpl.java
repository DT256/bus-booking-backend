package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.group8.busbookingbackend.dto.auth.request.UserLoginRequest;
import com.group8.busbookingbackend.dto.auth.response.AuthResponse;
import com.group8.busbookingbackend.entity.Role;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.entity.UserStatus;
import com.group8.busbookingbackend.exception.AppException;
import com.group8.busbookingbackend.exception.ErrorCode;
import com.group8.busbookingbackend.repository.UserRepository;
import com.group8.busbookingbackend.security.JwtProvider;
import com.group8.busbookingbackend.service.IAuthService;
import com.group8.busbookingbackend.service.IEmailService;
import com.group8.busbookingbackend.service.IOTPService;
import com.group8.busbookingbackend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOTPService otpService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Đăng ký tài khoản mới
    @Override
    public AuthResponse register(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXIST_REGISTER);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXIST_REGISTER);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail()).status(UserStatus.INACTIVE)
                .build();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        user = userRepository.save(user);
        String token = JwtProvider.generateJwtToken(user);
        emailService.sendOtp(request.getEmail(), otpService.generateOtp(request.getEmail()));
        return new AuthResponse(token, "Register successfully");
    }

    // Đăng nhập
    @Override
    public AuthResponse login(UserLoginRequest request) {
        User user = userService.findUserByEmail(request.getEmail());
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!user.getStatus().toString().equals("ACTIVE")) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }

        if (!matches) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = JwtProvider.generateJwtToken(user);
        return new AuthResponse(token, "Login successfully");
    }

    // Kích hoạt tài khoản qua OTP
    @Override
    @Transactional
    public boolean activateAccount(String email, String otp) {
        User user = userService.findUserByEmail(email);
        if (user != null && otpService.validateOtp(email, otp)) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            otpService.clearOtp(email);  // Xóa OTP sau khi kích hoạt thành công
            return true;
        }
        return false;
    }

    // Quên mật khẩu và gửi OTP
    @Override
    public boolean resetPassword(String email, String password) {
        return userService.updatePassword(email, password);
    }
}
