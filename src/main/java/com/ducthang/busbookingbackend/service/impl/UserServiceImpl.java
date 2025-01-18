package com.ducthang.busbookingbackend.service.impl;

import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.exception.AppException;
import com.ducthang.busbookingbackend.exception.ErrorCode;
import com.ducthang.busbookingbackend.repository.UserRepository;
import com.ducthang.busbookingbackend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXIST));
    }
}
