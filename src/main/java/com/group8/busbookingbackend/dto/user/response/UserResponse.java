package com.group8.busbookingbackend.dto.user.response;


import com.group8.busbookingbackend.entity.Role;
import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Address address;
    private String phoneNumber;
    private Boolean gender;
    private String avatarUrl;
    private Role role;


    @Data
    public static class Address{
        private String city;
        private String district;
        private String commune;
        private String other;
    }

}
