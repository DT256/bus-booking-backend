package com.group8.busbookingbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendOtp(String email, String otp){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Cấu hình nội dung email
            helper.setTo(email);
            helper.setSubject("Your OTP Code");
            helper.setText("Your OTP code is: " + otp);

            // Gửi email
            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + email);
            return true;
        } catch (MailException | MessagingException e) {
            System.err.println("Failed to send email to " + email);
            e.printStackTrace();
            return false;
        }
    }
}
