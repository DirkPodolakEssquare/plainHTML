package de.essquare.driftbottle;

import java.util.Objects;

public class ConversationDto {

    private String id;
    private String sender;
    private String text;
    private String timestamp;

    public ConversationDto() {
    }

    public ConversationDto(String id, String sender, String text, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversationDto that = (ConversationDto) o;
        return Objects.equals(id, that.id) && Objects.equals(sender, that.sender) && Objects.equals(text, that.text)
               && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, text, timestamp);
    }

    @Override
    public String toString() {
        return "ConversationDto{" +
               "id='" + id + '\'' +
               ", sender='" + sender + '\'' +
               ", text='" + text + '\'' +
               ", timestamp='" + timestamp + '\'' +
               '}';
    }

    public static ConversationDtoBuilder builder() {
        return ConversationDtoBuilder.builder();
    }

    public static final class ConversationDtoBuilder {

        private final ConversationDto conversationDto;

        private ConversationDtoBuilder() {
            conversationDto = new ConversationDto();
        }

        public static ConversationDtoBuilder builder() {
            return new ConversationDtoBuilder();
        }

        public ConversationDtoBuilder withId(String id) {
            conversationDto.setId(id);
            return this;
        }

        public ConversationDtoBuilder withSender(String sender) {
            conversationDto.setSender(sender);
            return this;
        }

        public ConversationDtoBuilder withText(String text) {
            conversationDto.setText(text);
            return this;
        }

        public ConversationDtoBuilder withTimestamp(String timestamp) {
            conversationDto.setTimestamp(timestamp);
            return this;
        }

        public ConversationDto build() {
            return conversationDto;
        }
    }
}
