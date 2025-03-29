package com.group8.busbookingbackend.repository;

import com.group8.busbookingbackend.entity.ChatEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface ChatRepository extends MongoRepository<ChatEntity, ObjectId> {
    @Aggregation(pipeline = {
            "{ $match: { receiverId: ?0 } }",
            "{ $lookup: { from: 'users', localField: 'senderId', foreignField: '_id', as: 'senderInfo' } }",
            "{ $unwind: { path: '$senderInfo', preserveNullAndEmptyArrays: true } }",
            "{ $group: { _id: '$senderId', name: { $first: { $ifNull: ['$senderInfo.name', { $toString: '$_id' }] } } } }",
            "{ $project: { _id: 0, userID: '$_id', name: 1 } }"
    })
    List<Map<String, Object>> findDistinctSendersByReceiverId(ObjectId receiverId);
}