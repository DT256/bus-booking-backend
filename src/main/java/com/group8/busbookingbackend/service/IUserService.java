package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.user.request.UpdateUserRequest;
import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.User;
import org.bson.types.ObjectId;

public interface IUserService {
    UserResponse findUserResponseByEmail(String email);
    User findUserByEmail(String email);
    public boolean updatePassword(String email, String password);
    UserResponse updateUser(ObjectId userId, UpdateUserRequest updatedUser);

    UserResponse findUserById(ObjectId userId);
}
