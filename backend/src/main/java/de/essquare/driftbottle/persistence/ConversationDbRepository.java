package de.essquare.driftbottle.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class ConversationDbRepository {

    private final DynamoDBMapper conversationDynamoDBMapper;
    private final DynamoDBMapperConfig conversationTableConfig;

    public ConversationDbRepository(DynamoDBMapper conversationDynamoDBMapper,
                                    DynamoDBMapperConfig conversationTableConfig) {
        this.conversationDynamoDBMapper = conversationDynamoDBMapper;
        this.conversationTableConfig = conversationTableConfig;
    }

    public List<ConversationEntity> getConversationsByReceiver(String receiver) {
        DynamoDBQueryExpression<ConversationEntity> queryExpression = new DynamoDBQueryExpression<ConversationEntity>()
                .withIndexName("receiver-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("receiver = :receiver")
                .withExpressionAttributeValues(Map.of(":receiver", new AttributeValue(receiver)))
                .withProjectionExpression("id");
        return conversationDynamoDBMapper.query(ConversationEntity.class, queryExpression, conversationTableConfig);
    }
}
