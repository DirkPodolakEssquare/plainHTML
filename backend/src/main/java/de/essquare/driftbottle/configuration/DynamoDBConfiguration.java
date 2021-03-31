package de.essquare.driftbottle.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

@Configuration
public class DynamoDBConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDBConfiguration.class);

    private final String dynamoDbEndpointUrl;
    private final String awsRegion;
    private final String userTableName;
    private final String conversationTableName;
    private final String messageTableName;

    public DynamoDBConfiguration(@Value("${aws.region}") String awsRegion,
                                 @Value("${aws.dynamo.endpoint.url}") String dynamoDbEndpointUrl,
                                 @Value("${aws.dynamo.user.tablename}") String userTableName,
                                 @Value("${aws.dynamo.conversation.tablename}") String conversationTableName,
                                 @Value("${aws.dynamo.message.tablename}") String messageTableName) {
        this.awsRegion = awsRegion;
        this.dynamoDbEndpointUrl = dynamoDbEndpointUrl;
        this.userTableName = userTableName;
        this.conversationTableName = conversationTableName;
        this.messageTableName = messageTableName;
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(dynamoDbEndpointUrl, awsRegion);
        return AmazonDynamoDBClientBuilder.standard()
                                          .withEndpointConfiguration(endpointConfiguration)
                                          .build();
    }

    @Bean
    public DynamoDBMapper userDynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDB(), userTableConfig());
    }

    @Bean
    public DynamoDBMapperConfig userTableConfig() {
        return DynamoDBMapperConfig.builder()
                                   .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(userTableName))
                                   .build();
    }

    @Bean
    public DynamoDBMapper conversationDynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDB(), conversationTableConfig());
    }

    @Bean
    public DynamoDBMapperConfig conversationTableConfig() {
        return DynamoDBMapperConfig.builder()
                                   .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(conversationTableName))
                                   .build();
    }

    @Bean
    public DynamoDBMapper messageDynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDB(), messageTableConfig());
    }

    @Bean
    public DynamoDBMapperConfig messageTableConfig() {
        return DynamoDBMapperConfig.builder()
                                   .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(messageTableName))
                                   .build();
    }
}
