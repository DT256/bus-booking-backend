package com.group8.busbookingbackend.dto.user.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateUserRequest {
    private String name;
    private LocalDateTime dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String avatarUrl;
    private String password;
}
