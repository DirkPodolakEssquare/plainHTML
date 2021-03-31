package de.essquare.driftbottle.persistence;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Message")
public class MessageEntity {

    private String id;
    private String conversation;
    private String author;
    private String content;
    private String created;

    public MessageEntity() {
    }

    public MessageEntity(String id, String conversation, String author, String content, String created) {
        this.id = id;
        this.conversation = conversation;
        this.author = author;
        this.content = content;
        this.created = created;
    }

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "conversation-index")
    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageEntity that = (MessageEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(conversation, that.conversation) && Objects.equals(author, that.author)
               && Objects.equals(content, that.content) && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, conversation, author, content, created);
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
               "id='" + id + '\'' +
               ", conversation='" + conversation + '\'' +
               ", author='" + author + '\'' +
               ", content='" + content + '\'' +
               ", created='" + created + '\'' +
               '}';
    }

    public static MessageEntityBuilder builder() {
        return MessageEntityBuilder.builder();
    }

    public static final class MessageEntityBuilder {

        private final MessageEntity messageEntity;

        private MessageEntityBuilder() {
            messageEntity = new MessageEntity();
        }

        public static MessageEntityBuilder builder() {
            return new MessageEntityBuilder();
        }

        public MessageEntityBuilder withId(String id) {
            messageEntity.setId(id);
            return this;
        }

        public MessageEntityBuilder withConversation(String conversation) {
            messageEntity.setConversation(conversation);
            return this;
        }

        public MessageEntityBuilder withAuthor(String author) {
            messageEntity.setAuthor(author);
            return this;
        }

        public MessageEntityBuilder withText(String text) {
            messageEntity.setContent(text);
            return this;
        }

        public MessageEntityBuilder withTimestamp(String timestamp) {
            messageEntity.setCreated(timestamp);
            return this;
        }

        public MessageEntity build() {
            return messageEntity;
        }
    }
}
