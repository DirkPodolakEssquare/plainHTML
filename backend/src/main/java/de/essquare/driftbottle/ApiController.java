package de.essquare.driftbottle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.essquare.driftbottle.service.ConversationService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    private final ConversationService conversationService;

    public ApiController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping(path = "/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConversationDto>> getConversations() {
        // ingress
        LOG.info("GET /api/conversations with ...");

        // no explicit validation

        // business logic
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        List<ConversationDto> conversations = conversationService.getConversations(authentication.getName());

        // egress
        LOG.info("GET /api/conversations successful");
        return ResponseEntity.status(HttpStatus.OK).body(conversations);
    }

}
