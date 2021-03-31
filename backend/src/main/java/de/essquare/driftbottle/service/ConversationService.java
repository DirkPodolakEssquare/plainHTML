package de.essquare.driftbottle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.essquare.driftbottle.ConversationDto;
import de.essquare.driftbottle.persistence.ConversationDbRepository;

@Service
public class ConversationService {

    private ConversationDbRepository conversationDbRepository;
    private ConversationMapper conversationMapper;

    public ConversationService(ConversationDbRepository conversationDbRepository,
                               ConversationMapper conversationMapper) {
        this.conversationDbRepository = conversationDbRepository;
        this.conversationMapper = conversationMapper;
    }

    public List<ConversationDto> getConversations(String userId) {
        return conversationMapper.map(conversationDbRepository.getConversationsByReceiver(userId));
    }
}
