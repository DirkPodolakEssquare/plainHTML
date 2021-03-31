package de.essquare.driftbottle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.essquare.driftbottle.ConversationDto;
import de.essquare.driftbottle.persistence.ConversationEntity;
import de.essquare.driftbottle.persistence.MessageDbRepository;
import de.essquare.driftbottle.persistence.MessageEntity;

@Service
public class ConversationMapper {

    private final MessageDbRepository messageDbRepository;

    public ConversationMapper(MessageDbRepository messageDbRepository) {
        this.messageDbRepository = messageDbRepository;
    }

    public List<ConversationDto> map(List<ConversationEntity> conversationEntities) {
        return conversationEntities
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public ConversationDto map(ConversationEntity conversationEntity) {
        MessageEntity latestMessage = messageDbRepository.getLatestMessageOfConversation(conversationEntity.getId());
        return ConversationDto.builder()
                              .withId(conversationEntity.getId())
                              .withSender(conversationEntity.getSender())
                              .withText(latestMessage.getContent())
                              .withTimestamp(latestMessage.getCreated())
                              .build();
    }

}
