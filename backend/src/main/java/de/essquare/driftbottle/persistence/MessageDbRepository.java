package de.essquare.driftbottle.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class MessageDbRepository {

    private final DynamoDBMapper messageDynamoDBMapper;
    private final DynamoDBMapperConfig messageTableConfig;

    public MessageDbRepository(DynamoDBMapper messageDynamoDBMapper,
                               DynamoDBMapperConfig messageTableConfig) {
        this.messageDynamoDBMapper = messageDynamoDBMapper;
        this.messageTableConfig = messageTableConfig;
    }

    public PaginatedQueryList<MessageEntity> getMessagesByConversation(String conversation) {
        DynamoDBQueryExpression<MessageEntity> queryExpression = new DynamoDBQueryExpression<MessageEntity>()
                .withIndexName("conversation-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("conversation = :conversation")
                .withExpressionAttributeValues(Map.of(":conversation", new AttributeValue(conversation)))
                .withProjectionExpression("content, created");
        return messageDynamoDBMapper.query(MessageEntity.class, queryExpression, messageTableConfig);
    }

    public MessageEntity getLatestMessageOfConversation(String conversation) {
        PaginatedQueryList<MessageEntity> paginatedMessageEntities = getMessagesByConversation(conversation);
        if (paginatedMessageEntities.isEmpty()) {
            return null;
        }
        paginatedMessageEntities.loadAllResults();

        ArrayList<MessageEntity> messageEntities = new ArrayList<>(paginatedMessageEntities);
        messageEntities.sort(Comparator.comparing(MessageEntity::getCreated).reversed());

        return messageEntities.get(0);
    }
}
