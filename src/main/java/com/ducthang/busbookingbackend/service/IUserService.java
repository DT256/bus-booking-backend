package com.ducthang.busbookingbackend.service;

import com.ducthang.busbookingbackend.dto.user.response.UserResponse;
import com.ducthang.busbookingbackend.entity.User;

public interface IUserService {
    UserResponse findUserResponseByEmail(String email);
    User findUserByEmail(String email);
}
