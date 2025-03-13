package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.user.request.UpdateUserRequest;
import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.exception.AppException;
import com.group8.busbookingbackend.exception.ErrorCode;
import com.group8.busbookingbackend.mapper.UserMapper;
import com.group8.busbookingbackend.repository.UserRepository;
import com.group8.busbookingbackend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public boolean updatePassword(String email, String password) {
        return userRepository.updateUserByEmail(email, password);
    }

    @Override
    public User updateUser(String email, UpdateUserRequest updatedUser) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Cập nhật thông tin từ updatedUser
                    existingUser.setName(updatedUser.getName());
                    existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
                    existingUser.setGender(updatedUser.getGender());
                    existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
                    existingUser.setPassword(updatedUser.getPassword());

                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

}
