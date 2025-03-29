package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.entity.ChatEntity;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface IChatService {
    List<Map<String, Object>> findDistinctSendersByReceiverId(ObjectId receiverId);

    ChatEntity save(ChatEntity chatEntity);

    List<ChatEntity> findAll();
}
