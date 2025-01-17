package com.ducthang.busbookingbackend.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private ObjectId id;

    private String name;
    private LocalDateTime dataOfBirth;
    private Boolean gender;

    private String username;

    private String email;

    private String password;

    private String phoneNumber;

    private String avatarUrl;

    private Role role;

    private UserStatus status;

    private Set<ObjectId> ticket = new HashSet<>();

    private Set<ObjectId> payment = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}



