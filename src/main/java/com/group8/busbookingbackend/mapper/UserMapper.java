package com.group8.busbookingbackend.mapper;

import com.group8.busbookingbackend.dto.user.response.UserResponse;
import com.group8.busbookingbackend.entity.User;
import org.bson.types.ObjectId;
import org.mapstruct.*;

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
