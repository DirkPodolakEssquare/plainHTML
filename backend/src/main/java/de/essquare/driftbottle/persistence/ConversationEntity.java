package de.essquare.driftbottle.persistence;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Conversation")
public class ConversationEntity {

    private String id;
    private String sender;
    private String receiver;
    private String status;

    public ConversationEntity() {
    }

    public ConversationEntity(String id, String sender, String receiver, String status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "receiver-index")
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversationEntity that = (ConversationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(sender, that.sender) && Objects.equals(receiver, that.receiver) && Objects.equals(
                status,
                that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, receiver, status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Conversation{" +
               "id='" + id + '\'' +
               ", sender='" + sender + '\'' +
               ", receiver='" + receiver + '\'' +
               ", status='" + status + '\'' +
               '}';
    }

    public static ConversationBuilder builder() {
        return ConversationBuilder.builder();
    }

    public static final class ConversationBuilder {

        private final ConversationEntity conversation;

        private ConversationBuilder() {
            conversation = new ConversationEntity();
        }

        public static ConversationBuilder builder() {
            return new ConversationBuilder();
        }

        public ConversationBuilder withId(String id) {
            conversation.setId(id);
            return this;
        }

        public ConversationBuilder withSender(String sender) {
            conversation.setSender(sender);
            return this;
        }

        public ConversationBuilder withReceiver(String receiver) {
            conversation.setReceiver(receiver);
            return this;
        }

        public ConversationBuilder withStatus(String status) {
            conversation.setStatus(status);
            return this;
        }

        public ConversationEntity build() {
            return conversation;
        }
    }
}
