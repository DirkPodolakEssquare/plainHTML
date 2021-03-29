package de.essquare.driftbottle;

import java.util.Objects;

public class ConversationDto {

    private String id;
    private String sender;
    private String text;
    private String timestamp;

    public ConversationDto() {
    }

    public ConversationDto(final String id, final String sender, final String text, final String timestamp) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ConversationDto that = (ConversationDto) o;
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
        private String id;
        private String sender;
        private String text;
        private String timestamp;

        private ConversationDtoBuilder() {}

        public static ConversationDtoBuilder builder() {
            return new ConversationDtoBuilder();
        }

        public ConversationDtoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ConversationDtoBuilder withSender(String sender) {
            this.sender = sender;
            return this;
        }

        public ConversationDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public ConversationDtoBuilder withTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ConversationDto build() {
            ConversationDto conversationDto = new ConversationDto();
            conversationDto.setId(id);
            conversationDto.setSender(sender);
            conversationDto.setText(text);
            conversationDto.setTimestamp(timestamp);
            return conversationDto;
        }
    }
}
