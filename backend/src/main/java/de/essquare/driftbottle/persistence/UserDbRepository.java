package de.essquare.driftbottle.persistence;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class UserDbRepository {

    private final DynamoDBMapper userDynamoDBMapper;
    private final DynamoDBMapperConfig userTableConfig;

    public UserDbRepository(DynamoDBMapper userDynamoDBMapper,
                            DynamoDBMapperConfig userTableConfig) {
        this.userDynamoDBMapper = userDynamoDBMapper;
        this.userTableConfig = userTableConfig;
    }

    public UserEntity getByEmail(String email) {
        DynamoDBQueryExpression<UserEntity> queryExpression = new DynamoDBQueryExpression<UserEntity>()
                .withIndexName("email-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("email = :email")
                .withExpressionAttributeValues(Map.of(":email", new AttributeValue(email)))
                .withProjectionExpression("userId, email");
        PaginatedQueryList<UserEntity> queryList = userDynamoDBMapper.query(UserEntity.class, queryExpression, userTableConfig);
        if (queryList.isEmpty()) {
            return null;
        }
        return queryList.get(0);
    }

    public void save(UserEntity user) {
        userDynamoDBMapper.save(user, userTableConfig);
    }

    public UserEntity load(String userId) {
        return userDynamoDBMapper.load(UserEntity.class, userId, userTableConfig);
    }
}
