package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.exception.AppException;
import com.group8.busbookingbackend.exception.ErrorCode;
import com.group8.busbookingbackend.mapper.UserMapper;
import com.group8.busbookingbackend.repository.UserRepository;
import com.group8.busbookingbackend.service.IUserService;
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
