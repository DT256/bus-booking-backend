package com.ducthang.busbookingbackend.service.impl;

import com.ducthang.busbookingbackend.dto.user.response.UserResponse;
import com.ducthang.busbookingbackend.entity.User;
import com.ducthang.busbookingbackend.exception.AppException;
import com.ducthang.busbookingbackend.exception.ErrorCode;
import com.ducthang.busbookingbackend.mapper.UserMapper;
import com.ducthang.busbookingbackend.repository.UserRepository;
import com.ducthang.busbookingbackend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserResponse findUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toUserResponse(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXIST));
    }

}
