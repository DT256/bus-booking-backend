package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;
import com.group8.busbookingbackend.dto.user.request.UpdateUserRequest;
import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.security.JwtProvider;
import com.group8.busbookingbackend.service.IUserService;
import com.group8.busbookingbackend.service.impl.UserServiceImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private IUserService userServiceImpl;

    @GetMapping("/")
    public ApiResponse<UserResponse> getUserProfile(@RequestParam("email") String email){
        UserResponse user = userServiceImpl.findUserResponseByEmail(email);
        return ApiResponse.success(user, "Fetched user successfully");
    }
    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(@RequestHeader("Authorization") String authorizationHeader){
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);
        UserResponse user = userServiceImpl.findUserById(userId);
        return ApiResponse.success(user, "Fetched user successfully");
    }

//    @PutMapping("/update")
//    public ApiResponse<User> updateUser(@RequestParam("email") String email,
//                                        @RequestBody UpdateUserRequest updateRequest){
//        User user = userServiceImpl.updateUser(email, updateRequest);
//        return ApiResponse.success(user,"Update user successfully");
//    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ApiResponse<UserResponse> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("data") UpdateUserRequest requestDto,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);

        // Gán ảnh vào requestDto
        requestDto.setAvatar(avatar);

        UserResponse responseDto = userServiceImpl.updateUser(userId, requestDto);
        return ApiResponse.success(responseDto, "Profile updated successfully");
    }



}
