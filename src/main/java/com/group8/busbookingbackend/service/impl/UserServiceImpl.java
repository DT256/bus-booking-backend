package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.dto.user.request.UpdateUserRequest;
import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.AddressEntity;
import com.group8.busbookingbackend.entity.User;
import com.group8.busbookingbackend.exception.AppException;
import com.group8.busbookingbackend.exception.ErrorCode;
import com.group8.busbookingbackend.mapper.UserMapper;
import com.group8.busbookingbackend.repository.UserRepository;
import com.group8.busbookingbackend.service.ICloudinaryService;
import com.group8.busbookingbackend.service.IUserService;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ICloudinaryService cloudinaryService;

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
    public boolean updatePassword(String email, String newPassword) {
        Query query = new Query(Criteria.where("email").is(email));
        String password = (passwordEncoder.encode(newPassword));
        Update update = new Update().set("password", password);
        UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);
        return result.getModifiedCount() > 0;
    }

    @Override
    public UserResponse updateUser(ObjectId userId, UpdateUserRequest updatedUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        String avatarUrl = null;
        if (updatedUser.getAvatar() != null && !updatedUser.getAvatar().isEmpty()) {
            try {
                avatarUrl = cloudinaryService.uploadImage(updatedUser.getAvatar());
                user.setAvatarUrl(avatarUrl); // cập nhật avatar cho user
            } catch (IOException e) {
                throw new RuntimeException("Error uploading avatar image: " + updatedUser.getAvatar().getOriginalFilename(), e);
            }
        }

        if (updatedUser.getUsername() != null) user.setUsername(updatedUser.getUsername());
        if (updatedUser.getPhoneNumber() != null) user.setPhoneNumber(updatedUser.getPhoneNumber());
        if (updatedUser.getGender() != null) user.setGender(updatedUser.getGender());
        if (updatedUser.getDateOfBirth() != null) user.setDateOfBirth(updatedUser.getDateOfBirth());

        // Cập nhật địa chỉ
        if (updatedUser.getAddress() != null) {
            AddressEntity address = new AddressEntity();
            address.setCity(updatedUser.getAddress().getCity());
            address.setDistrict(updatedUser.getAddress().getDistrict());
            address.setCommune(updatedUser.getAddress().getCommune());
            address.setOther(updatedUser.getAddress().getOther());
            user.setAddress(address);
        }

        userRepository.save(user);

        // Trả về DTO
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getId().toHexString());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setUsername(user.getUsername());
        response.setGender(user.getGender());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole());
        response.setDateOfBirth(user.getDateOfBirth());

        if (user.getAddress() != null) {
            UserResponse.Address address = new UserResponse.Address();
            address.setCity(user.getAddress().getCity());
            address.setDistrict(user.getAddress().getDistrict());
            address.setCommune(user.getAddress().getCommune());
            address.setOther(user.getAddress().getOther());
            response.setAddress(address);
        }
        return response;
    }

    @Override
    public UserResponse findUserById(ObjectId userId) {
        User user = userRepository.findById(userId).orElseThrow(()->
                new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toUserResponse(user);
    }

    @Override
    public boolean changePassword(ObjectId userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // Mã hóa và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

}
