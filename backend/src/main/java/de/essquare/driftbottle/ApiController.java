package de.essquare.driftbottle;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
