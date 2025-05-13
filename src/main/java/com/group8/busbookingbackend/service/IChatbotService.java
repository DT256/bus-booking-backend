package com.group8.busbookingbackend.service;

import com.group8.busbookingbackend.dto.chatbot.ChatMessage;
import org.bson.types.ObjectId;

public interface IChatbotService {
    public ChatMessage processMessage(ObjectId userId, String content);
}
