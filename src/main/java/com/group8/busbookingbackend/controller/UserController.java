package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.ApiResponse;
import com.group8.busbookingbackend.dto.user.request.UpdateUserRequest;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.service.IUserService;
import com.group8.busbookingbackend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired

    private IUserService userServiceImpl;

    @PutMapping("/update")
    public ApiResponse<User> updateUser(@RequestParam("email") String email,
                                        @RequestBody UpdateUserRequest updateRequest){
        User user = userServiceImpl.updateUser(email, updateRequest);
        return ApiResponse.success(user,"Update user successfully");
    }



}
