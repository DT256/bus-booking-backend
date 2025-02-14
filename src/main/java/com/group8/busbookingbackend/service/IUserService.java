package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.User;

public interface IUserService {
    UserResponse findUserResponseByEmail(String email);
    User findUserByEmail(String email);
}
