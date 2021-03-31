package de.essquare.driftbottle;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.essquare.driftbottle.persistence.UserEntity;
import de.essquare.driftbottle.service.ConversationService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    private final UserService userService;
    private final ConversationService conversationService;

    public ApiController(UserService userService, ConversationService conversationService) {
        this.userService = userService;
        this.conversationService = conversationService;
    }

    @PostMapping(path = "user")
    public ResponseEntity<Void> createUser(@RequestBody Map<String, String> data) {
        // ingress
        LOG.info("POST /api/user with {}", data);

        // validation
        String email = data.get(UserEntity.EMAIL_KEY);
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // business logic
        userService.postEmail(data.get("email"));

        // egress
        LOG.info("POST /api/user successful");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_http://driftbottle.eu-central-1.elasticbeanstalk.com/api/user.read')")
    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntity> getUser(@RequestParam String userId) {
        // ingress
        LOG.info("GET /api/user with userId {}", userId);

        // no explicit validation

        // business logic
        UserEntity user = userService.getUser(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // egress
        LOG.info("GET /api/user successful");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PreAuthorize("hasAuthority('SCOPE_http://driftbottle.eu-central-1.elasticbeanstalk.com/api/user.read')")
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
