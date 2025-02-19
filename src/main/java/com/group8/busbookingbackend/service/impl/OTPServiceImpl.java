package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.service.IEmailService;
import com.group8.busbookingbackend.service.IOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPServiceImpl implements IOTPService {
    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, Long> otpExpirationStore = new HashMap<>();

    // Thời gian hết hạn của OTP (ví dụ 5 phút)
    private final long OTP_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(5);

    @Autowired
    IEmailService emailService;

    // Tạo OTP và gửi qua email
    @Override
    public String generateOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(999999 - 100000) + 100000); // 6 chữ số
        otpStore.put(email, otp);  // Lưu OTP
        otpExpirationStore.put(email, System.currentTimeMillis() + OTP_EXPIRATION_TIME); // Lưu thời gian hết hạn
        return otp;
    }

    @Override
    public String sendOtp(String email){
        String otp = this.generateOtp(email);
        boolean isEmailSent = emailService.sendOtp(email,otp);
        return isEmailSent ? "Send mail successfully" : "Send mail failed";

    }

    // Kiểm tra OTP có hợp lệ không
    @Override
    public boolean validateOtp(String email, String otp) {
        // Kiểm tra OTP có tồn tại không
        if (!otpStore.containsKey(email)) {
            return false;
        }

        // Kiểm tra OTP có hết hạn không
        if (System.currentTimeMillis() > otpExpirationStore.get(email)) {
            otpStore.remove(email); // Xóa OTP đã hết hạn
            otpExpirationStore.remove(email);
            return false;
        }

        // Kiểm tra OTP có khớp không
        return otp.equals(otpStore.get(email));
    }

    // Xóa OTP sau khi xác thực thành công
    @Override
    public void clearOtp(String email) {
        otpStore.remove(email);
        otpExpirationStore.remove(email);
    }
}
