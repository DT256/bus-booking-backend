package com.ducthang.busbookingbackend.mapper;

import com.ducthang.busbookingbackend.dto.auth.request.UserCreateRequest;
import com.ducthang.busbookingbackend.dto.user.request.UserRequest;
import com.ducthang.busbookingbackend.dto.user.response.UserDataResponse;
import com.ducthang.busbookingbackend.dto.user.response.UserResponse;
import com.ducthang.busbookingbackend.entity.User;
import org.bson.types.ObjectId;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper
{
    @Mapping(source = "id", target = "userId", qualifiedByName = "objectIdToString")
    UserResponse toUserResponse(User user);

    @Named("objectIdToString")
    static String objectIdToString(ObjectId objectId) {
        return objectId != null ? objectId.toHexString() : null;
    }

}
