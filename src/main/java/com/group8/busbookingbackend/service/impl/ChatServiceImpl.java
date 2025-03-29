package com.group8.busbookingbackend.service.impl;

import com.group8.busbookingbackend.entity.ChatEntity;
import com.group8.busbookingbackend.repository.ChatRepository;
import com.group8.busbookingbackend.service.IChatService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements IChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public List<Map<String, Object>> findDistinctSendersByReceiverId(ObjectId receiverId) {
        if (receiverId == null) {
            throw new IllegalArgumentException("Receiver ID cannot be null");
        }
        return chatRepository.findDistinctSendersByReceiverId(receiverId);
    }

    @Override
    public ChatEntity save(ChatEntity chatEntity) {
        if (chatEntity == null || chatEntity.getContentMessage() == null || chatEntity.getContentMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Chat entity or message content cannot be null or empty");
        }
        if (chatEntity.getSenderId() == null || chatEntity.getReceiverId() == null) {
            throw new IllegalArgumentException("Sender ID and Receiver ID must be provided");
        }
        return chatRepository.save(chatEntity);
    }

    @Override
    public List<ChatEntity> findAll() {
        return chatRepository.findAll();
    }
}