package com.group8.busbookingbackend.controller;

import com.group8.busbookingbackend.entity.ChatEntity;
import com.group8.busbookingbackend.service.IChatService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private IChatService chatService;

    private static final ObjectId ADMIN_ID = new ObjectId("65f4d8b2e4b0a56a4e3f3456");

    @GetMapping("/getCustomerList")
    public ResponseEntity<List<Map<String, Object>>> getCustomerList(@RequestParam(required = false) ObjectId receiverId) {
        try {
            ObjectId targetReceiverId = (receiverId != null) ? receiverId : ADMIN_ID;
            List<Map<String, Object>> customers = chatService.findDistinctSendersByReceiverId(targetReceiverId);
            return ResponseEntity.ok(customers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatEntity sendMessage(ChatEntity chatEntity) {
        ObjectId senderID = chatEntity.getSenderId();
        ObjectId receiverID = chatEntity.getReceiverId();

        boolean isCustomerSendingToAdmin = isCustomer(senderID) && isAdmin(receiverID);
        boolean isAdminSendingToCustomer = isAdmin(senderID) && isCustomer(receiverID);

        if (!isCustomerSendingToAdmin && !isAdminSendingToCustomer) {
            throw new IllegalArgumentException("Quyền truy cập không hợp lệ");
        }

        return chatService.save(chatEntity);
    }

    @GetMapping("/getMessages")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> loadMessage(
            @RequestParam ObjectId senderId,
            @RequestParam ObjectId receiverId) {
        try {
            if (senderId == null || receiverId == null) {
                return ResponseEntity.badRequest().body(null);
            }
            List<Map<String, Object>> messages = chatService.findAll().stream()
                    .filter(row -> (row.getSenderId().equals(senderId) && row.getReceiverId().equals(receiverId)) ||
                            (row.getSenderId().equals(receiverId) && row.getReceiverId().equals(senderId)))
                    .map(row -> {
                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("id", row.getChatId());
                        messageMap.put("contentMessage", row.getContentMessage());
                        messageMap.put("timestamp", row.getCreatedAt());
                        messageMap.put("senderID", row.getSenderId());
                        messageMap.put("receiverID", row.getReceiverId());
                        return messageMap;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private boolean isCustomer(ObjectId userID) {
        return userID != null && !userID.equals(ADMIN_ID);
    }

    private boolean isAdmin(ObjectId userID) {
        return userID != null && userID.equals(ADMIN_ID);
    }
}