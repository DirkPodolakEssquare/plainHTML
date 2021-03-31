#!/bin/bash

set -ex

docker-compose down

export AWS_PAGER=""
export AWS_MAX_ATTEMPTS=10
export AWS_RETRY_MODE=standard
export DEFAULT_REGION=eu-central-1
export SERVICES=dynamodb
docker-compose up -d
echo "local setup started successfully"

echo "Creating DynamoDb-Table User"
aws dynamodb create-table \
    --endpoint-url http://localhost:5569 \
    --table-name User \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
    --key-schema AttributeName=userId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

aws dynamodb update-table \
    --endpoint-url http://localhost:5569 \
    --table-name User \
    --attribute-definitions AttributeName=email,AttributeType=S \
    --global-secondary-index-updates \
    "[{\"Create\":{\"IndexName\": \"email-index\",\"KeySchema\":[{\"AttributeName\":\"email\",\"KeyType\":\"HASH\"}], \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 10, \"WriteCapacityUnits\": 5}, \"Projection\":{\"ProjectionType\":\"ALL\"}}}]"
echo "Successfully created DynamoDb-Table User."

aws --endpoint-url=http://localhost:5569 dynamodb scan --table-name User

# Conversation
aws dynamodb create-table \
    --endpoint-url http://localhost:5569 \
    --table-name Conversation \
    --attribute-definitions AttributeName=id,AttributeType=S \
                            AttributeName=receiver,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --global-secondary-indexes \
        "[
            {
                \"IndexName\": \"receiver-index\",
                \"KeySchema\": [{\"AttributeName\":\"receiver\",\"KeyType\":\"HASH\"}],
                \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 10, \"WriteCapacityUnits\": 5},
                \"Projection\":{
                    \"ProjectionType\":\"ALL\"
                }
            }
        ]"

aws --endpoint-url http://localhost:5569/ dynamodb put-item --table-name Conversation --item '{"id":{"S":"conversationId1"},"sender":{"S":"sender1"},"receiver":{"S":"c1e211ff-b623-4860-9d86-b6f534e9d747"},"status":{"S":"active"}}'

aws --endpoint-url=http://localhost:5569 dynamodb scan --table-name Conversation

aws dynamodb query \
    --endpoint-url http://localhost:5569 \
    --table-name Conversation \
    --index-name receiver-index \
    --key-condition-expression "receiver = :receiver" \
    --expression-attribute-values '{":receiver":{"S":"c1e211ff-b623-4860-9d86-b6f534e9d747"} }'

#aws dynamodb delete-table \
#    --endpoint-url http://localhost:5569 \
#    --table-name Conversation

# Message
aws dynamodb create-table \
    --endpoint-url http://localhost:5569 \
    --table-name Message \
    --attribute-definitions AttributeName=id,AttributeType=S \
                            AttributeName=conversation,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --global-secondary-indexes \
        "[
            {
                \"IndexName\": \"conversation-index\",
                \"KeySchema\": [{\"AttributeName\":\"conversation\",\"KeyType\":\"HASH\"}],
                \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 10, \"WriteCapacityUnits\": 5},
                \"Projection\":{
                    \"ProjectionType\":\"INCLUDE\",
                    \"NonKeyAttributes\":[\"content\", \"created\"]
                }
            }
        ]"

aws --endpoint-url http://localhost:5569/ dynamodb put-item --table-name Message --item '{"id":{"S":"messageId1"},"conversation":{"S":"conversationId1"},"author":{"S":"sender1"},"content":{"S":"Hallo du."},"created":{"S":"2021-03-31T12:00:00Z"}}'
aws --endpoint-url http://localhost:5569/ dynamodb put-item --table-name Message --item '{"id":{"S":"messageId2"},"conversation":{"S":"conversationId1"},"author":{"S":"sender1"},"content":{"S":"Wie gehts?"},"created":{"S":"2021-03-31T12:30:00Z"}}'

aws --endpoint-url=http://localhost:5569 dynamodb scan --table-name Message

aws dynamodb query \
    --endpoint-url http://localhost:5569 \
    --table-name Message \
    --index-name conversation-index \
    --key-condition-expression "conversation = :conversation" \
    --expression-attribute-values '{":conversation":{"S":"conversationId1"} }'

#aws dynamodb delete-table \
#    --endpoint-url http://localhost:5569 \
#    --table-name Message