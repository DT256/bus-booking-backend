package com.ducthang.busbookingbackend.dto.user.response;

import com.ducthang.socialnetwork.entity.Role;
import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String description;
    private Boolean gender;
    private String avatarUrl;
    private String coverPhotoUrl;
    private Role role;

}
