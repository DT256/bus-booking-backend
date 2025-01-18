package com.ducthang.busbookingbackend.service;

import com.ducthang.busbookingbackend.entity.User;

public interface IUserService {
    User findUserByEmail(String email);
}
