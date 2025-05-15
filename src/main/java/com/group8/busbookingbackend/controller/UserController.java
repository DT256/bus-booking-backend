package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.review.request.ReviewRequest;
import com.group8.busbookingbackend.dto.review.response.ReviewResponse;
import com.group8.busbookingbackend.dto.user.request.ChangePasswordRequest;
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

    // API 1: Lưu avatar riêng
    @PutMapping(value = "/avatar", consumes = {"multipart/form-data"})
    public ApiResponse<UserResponse> updateAvatar(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("avatar") MultipartFile avatar) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);

        if (avatar == null || avatar.isEmpty()) {
            return ApiResponse.error( 400, "Avatar file is required", null);
        }

        UpdateUserRequest requestDto = new UpdateUserRequest();
        requestDto.setAvatar(avatar);

        UserResponse responseDto = userServiceImpl.updateUser(userId, requestDto);
        return ApiResponse.success(responseDto, "Avatar updated successfully");
    }

    // API 2: Lưu thông tin cá nhân (họ tên, số điện thoại, ngày sinh, giới tính)
    @PutMapping("/personal-info")
    public ApiResponse<UserResponse> updatePersonalInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateUserRequest requestDto) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);

        // Đảm bảo chỉ cập nhật các trường thông tin cá nhân, không bao gồm avatar
        requestDto.setAvatar(null);

        UserResponse responseDto = userServiceImpl.updateUser(userId, requestDto);
        return ApiResponse.success(responseDto, "Personal info updated successfully");
    }

    // API 3: Đổi mật khẩu
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            return ApiResponse.error(400,"New password and confirmation do not match", null);
        }

        boolean success = userServiceImpl.changePassword(userId, changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());

        if (success) {
            return ApiResponse.success("Password changed successfully", "Password changed successfully");
        } else {
            return ApiResponse.error(400,"Old password is incorrect",null);
        }
    }

}
