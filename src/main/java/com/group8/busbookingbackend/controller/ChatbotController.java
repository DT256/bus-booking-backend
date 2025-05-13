package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.dto.chatbot.ChatMessage;
import com.group8.busbookingbackend.security.JwtProvider;
import com.group8.busbookingbackend.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final IChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChatMessage message) {
        String strUserId = JwtProvider.getUserIdFromToken(authorizationHeader);
        ObjectId userId = new ObjectId(strUserId);

        ChatMessage response = chatbotService.processMessage(userId, message.getContent());
        return ResponseEntity.ok(response);
    }
} 