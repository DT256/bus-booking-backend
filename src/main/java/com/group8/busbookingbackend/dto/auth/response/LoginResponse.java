package com.group8.busbookingbackend.dto.auth.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String jwt;
    private String message;
    private String username;
    private String avatarUrl;
    private String phoneNumber;
    private String gender;
    private LocalDateTime dateOfBirth;
}
