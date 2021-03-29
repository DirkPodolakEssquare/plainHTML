package de.essquare.driftbottle;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    private final Service service;

    public ApiController(Service service) {
        this.service = service;
    }

    @PostMapping(path = "user")
    public ResponseEntity<Void> createUser(@RequestBody Map<String, String> data) {
        // ingress
        LOG.info("POST /api/user with {}", data);

        // validation
        String email = data.get(User.EMAIL_KEY);
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // business logic
        service.postEmail(data.get("email"));

        // egress
        LOG.info("POST /api/user successful");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@RequestParam String userId) {
        // ingress
        LOG.info("GET /api/user with userId {}", userId);

        // no explicit validation

        // business logic
        User user = service.getUser(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // egress
        LOG.info("GET /api/user successful");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping(path = "/conversations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConversationDto>> getConversations(@AuthenticationPrincipal Jwt principal) {

        System.out.println(principal);

        // ingress
        LOG.info("GET /api/conversations with id {}", principal.getId());

        // no explicit validation

        // business logic
//        User user = service.getUser(userId);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }

        // egress
        LOG.info("GET /api/conversations successful");
        return ResponseEntity.status(HttpStatus.OK).body(List.of(ConversationDto.builder()
                                                                                .withId("testId")
                                                                                .withSender(UUID.randomUUID().toString())
                                                                                .withText("some text")
                                                                                .withTimestamp("2021-03-29T09:09:00Z")
                                                                                .build()));
    }
}
