package com.ducthang.busbookingbackend.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, Long> otpExpirationStore = new HashMap<>();

    // Thời gian hết hạn của OTP (ví dụ 5 phút)
    private final long OTP_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(5);

    // Tạo OTP và gửi qua email
    public String generateOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(999999 - 100000) + 100000); // 6 chữ số
        otpStore.put(email, otp);  // Lưu OTP
        otpExpirationStore.put(email, System.currentTimeMillis() + OTP_EXPIRATION_TIME); // Lưu thời gian hết hạn
        return otp;
    }

    // Kiểm tra OTP có hợp lệ không
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
    public void clearOtp(String email) {
        otpStore.remove(email);
        otpExpirationStore.remove(email);
    }
}
